(ns pomodoro-page
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [clojure.string :refer [join]]
   [date-fns :as date-fns]
   ["react-tooltip" :as ReactTooltip]
   [util.time :refer [diff-in-duration humanize-double-digit]]
   [util.dev :refer [dev-panel]]
   [component.timer :as clock]
   [component.input :as input]
   [component.icon :as icon]
   [component.vis :as vis]
   [component.style :refer [btn-invalid fn-animate-css]]))

; TOOD: All time components - ms-placement "right", due to flexbox rule will keep adjusting, aka timer shaking right and left. Using bottom only for now.


; Logic here is earlier, simpler, not as good as advance version. Less features.
(defn pomodoro-simple []
  (reagent/with-let [state (reagent/atom {; For visual only. Automatically mutated in system.
                                          :start (.now js/Date)
                                          :end (date-fns/addMinutes (.now js/Date) 25)

                                          :ms-visible? false
                                          :ms-placement "bottom"

                                          ; Required due to user interactions
                                          :clean? true ; Inital state of running not have happened at all. E.g user interaction Clean
                                          :running? false
                                          :finished? false ; Timer has finished (Reached 0). 
                                          :break? false; Tracking break

                                          ;; for calculating break length. Due to lack of duration concept and being able to use, it needs to be stored this way
                                          :value-break-start (.now js/Date) ; Doesn't mutate in length calc logic (although logic can, my design I only modify end.)
                                          :value-break-end (date-fns/addMinutes (.now js/Date) 5)  ;default break of 5 minutes. Gives 4. diff in ms must be rounded down.

                                          ;; for calculating next pomodoro length. 25 by default.
                                          :value-next-start (date-fns/addMinutes (.now js/Date) 0) ; Doesn't mutate in length calc logic (although logic can, my design I only modify end.)
                                          :value-next-end (date-fns/addMinutes (.now js/Date) 25)

                                          :pomo-count 0
                                          :dev? (rf/subscribe [:dev?])}) ; Seems not reactive if I destructure here

                     alarm-ref (reagent/atom nil)

                     fn-play-alarm (fn [] (when-let [ref @alarm-ref]
                                            (if (.-paused ref)
                                              (.play ref)
                                              (.pause ref))))

                     fn-load-alarm (fn [] (when-let [ref @alarm-ref] ;; not nil?
                                            (.load ref)))

                     timer-id (reagent/atom nil) ;setInterval id used to clear interval

                     timer-fn (fn [] (js/setInterval
                                      (fn []
                                            ; Start will always be now. This will cause end - start calculation to change hence countdown (or countup). Based on this change calculation and conditions of app
                                            ; it will cause app to react
                                        (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))

                                            ; When end-start = 0, trigger finished. This could be session or break. Sets next timer. This should only run when time is at 0.
                                        (when (>= 0 (date-fns/differenceInSeconds (get-in @state [:end]) (get-in @state [:start])))
                                          (if (get-in @state [:break?])
                                            ((fn []
                                                   ; Case break timer has reached zero. Need to setup system to star normal session timer.
                                               (swap! state assoc-in [:finished?] false)
                                               (swap! state assoc-in [:break?] false)
                                               (fn-play-alarm)  ; freeCodecamp Requirement. Design decision. I want to trigger this through finished?. However I'll have to convert this to form-3 and sort after html has mounted. This is easier to do without converting.

                                                 ; Set start to 0, end to break (default is 5). Working out difference in minute of what user inserted
                                               (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))
                                               (swap! state assoc-in [:end] (date-fns/addMinutes (.now js/Date)
                                                                                                 (date-fns/differenceInMinutes (get-in @state [:value-next-end]) (get-in @state [:value-next-start]))))))

                                            ((fn []
                                                   ; Case finished session
                                               (swap! state assoc-in [:finished?] true)
                                               (swap! state assoc-in [:break?] true)
                                               (fn-play-alarm)

                                                 ; Set start to 0, end to break (default is 5). Working out difference in minute of what user inserted
                                               (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))
                                               (swap! state assoc-in [:end] (date-fns/addMinutes (.now js/Date)
                                                                                                 (date-fns/differenceInMinutes (get-in @state [:value-break-end]) (get-in @state [:value-break-start]))))


                                               ; Increase pomodoro count (for icon-array)
                                               (swap! state update-in [:pomo-count] inc))))))
                                      70))
                     ; Resets 
                     fn-reset (fn [e]
                                (let [next-pomo-length (date-fns/differenceInMinutes (get-in @state [:value-next-end]) (get-in @state [:value-next-start]))]
                                  (js/clearInterval @timer-id)
                                  (swap! state assoc-in [:clean?] true)
                                  (swap! state assoc-in [:running?] false)
                                  (swap! state assoc-in [:finished?] false)
                                  (swap! state assoc-in [:break?] false)

                                  (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))  ; This needs to be reset to now.
                                  ;; (swap! state assoc-in [:end] (date-fns/addMinutes (.now js/Date) next-pomo-length)) ;; My preferred logic. Insert this and remove freeCodeCamp Requirement below to revert back.

                                  ;;;;;;;;;;;
                                  ;; freeCodeCamp Requirement (temp  This logic is overriding my architecture "next-pomo-length")


                                  ; Reset pomodoro length timer
                                  (swap! state assoc-in [:value-next-start] (date-fns/addMinutes (.now js/Date) 0))  ; reset next timer to now. This logic is overriding my architecture "next-pomo-length"
                                  (swap! state assoc-in [:value-next-end] (date-fns/addMinutes (.now js/Date) 25))  ; reset next timer to 25 minutes This logic is overriding my architecture "next-pomo-length"
                                  (swap! state assoc-in [:end] (date-fns/addMinutes (.now js/Date) 25))  ; reset (force) next time to be 25 minutes. This logic is overriding my architecture "next-pomo-length"
                                  ;;;;;;;;;;;

                                  ; Reset break timer.
                                  (swap! state assoc-in [:value-break-start] (date-fns/addMinutes (.now js/Date) 0))
                                  (swap! state assoc-in [:value-break-end] (date-fns/addMinutes (.now js/Date) 5))

                                  ; Rewind 'Load' alarm
                                  (fn-load-alarm)

                                  ; Reset Pomodoro count "icon-array". TODO: Disabled for now. Reload the age instead to reset fully
                                  #_(swap! state assoc-in [:pomo-count] 0)))
                     
                     ;;  Initial Start
                     fn-start (fn [e]
                                (let [pomo-next-length (date-fns/differenceInMinutes (get-in @state [:value-next-end]) (get-in @state [:value-next-start]))]
                                  (swap! state assoc-in [:clean?] false)
                                  (swap! state assoc-in [:running?] true)
                                  (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))
                                  (swap! state assoc-in [:end] (date-fns/addMinutes (.now js/Date) pomo-next-length))
                                  ; (swap! state assoc-in [:end] (date-fns/addSeconds (.now js/Date) 5)) ; *** Modify to change end time
                                  (swap! timer-id timer-fn)))

                     ; This function is not callable in button when inappropriate
                     ; Calculation in millisecond as it can be paused at resumed in this scale
                     fn-resume (fn [e]
                                 (let [pomo-left-length (date-fns/differenceInMilliseconds (get-in @state [:end]) (get-in @state [:start]))]
                                   (swap! state assoc-in [:clean?] false)
                                   (swap! state assoc-in [:running?] true)
                                   (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))
                                   (swap! state assoc-in [:end] (date-fns/addMilliseconds (.now js/Date) pomo-left-length))
                                   (swap! timer-id timer-fn)))

                     fn-pause (fn [e]
                                (js/clearInterval @timer-id)
                                (swap! state assoc-in [:running?] false))

                     title-atom (reagent/atom nil)]

    (let [compound-duration-all (diff-in-duration (get-in @state [:end]) (get-in @state [:start]))
          compound-duration-filtered (dissoc compound-duration-all :w :d :h)
          ms (when (get-in @state [:running?]) (mod (date-fns/differenceInMilliseconds (get-in @state [:end]) (get-in @state [:start])) 1000))
          compound-duration-plus-ms (assoc compound-duration-filtered :ms ms)
          ; compound-duration contains logic for what shows up inside of main timer.
          compound-duration (cond
                              (get-in @state [:finished?]) {:m 0 :s 0 :ms 0}
                              (get-in @state [:running?]) compound-duration-plus-ms
                              :else compound-duration-plus-ms)

          clean? (get-in @state [:clean?])
          running? (get-in @state [:running?])
          finished? (get-in @state [:finished?])
          break? (get-in @state [:break?])

          pomo-count (get-in @state [:pomo-count])]

      [:div.flex.flex-col.items-center.justify-center.content-center.self-center
       (let [class-bg @(rf/subscribe [:theme/general-bg 100])
             class-text @(rf/subscribe [:theme/general-text 400])
             hover (str "hover:" class-bg)
             class (join  " " ["mb-3" "rounded" hover class-text])]
         [input/title {:value @title-atom
                       :class class
                       :on-change (fn [e] (reset! title-atom (-> e .-target .-value)))}])
       [:div.flex.flex-row.text-6xl.tracking-wide.leading-none.text-opacity-100.cursor-pointer.select-none
        {:on-click #(swap! state update-in [:ms-visible?] not)}
        [clock/digital-clean {:compound-duration compound-duration
                              :ms-placement (get-in @state [:ms-placement])
                              :ms-visible? (get-in @state [:ms-visible?])}]
        #_[:div.text-base.tracking-wide.leading-none.text-opacity-100.mt-2 ms]] ; I like side but it keeps changing, due to flex being responsive and fontsize being different. Maybe float or span?
       (when (get-in @state [:ms?]) [:div.text-base.tracking-wide.leading-none.text-opacity-100.mt-2 ms])
       [:div.flex.flex-row.mt-10.text-xl.transition-25to100.opacity-50
        [:button#reset.btn.btn-nav.mr-8 {:on-click (fn [e] (fn-reset e))} [icon/stop]]
        [:button#start_stop.btn.btn-nav {:on-click (fn [e] (if clean?
                                                             (fn-start e)
                                                             (if running? (fn-pause e) (fn-resume e))))
                                         :disabled finished?
                                         :class (when finished? "cursor-not-allowed opacity-50")}
         (if clean?
           [icon/play]
           (if running? [icon/pause] [icon/play]))]]
       [vis/icon-array {:num pomo-count :class "mt-4 font-normal"}]
       [:audio {:ref (fn [e] (reset! alarm-ref e))
                :id "beep"
                :preload "auto"
                :src "/static/Baoding-balls-ding.mp3"
                :controls false}]
       (when @(get-in @state [:dev?]) [dev-panel [state timer-id]])])
    (finally (js/clearInterval @timer-id))))


(defn pomodoro-simple--options []
  (reagent/with-let [state (reagent/atom {; For visual only. Automatically mutated in system.
                                          :start (date-fns/addMinutes (.now js/Date) 0)
                                          :end (date-fns/addMinutes (.now js/Date) 25)

                                          :ms-visible? false
                                          :ms-placement "bottom"  ; bottom, right, nil

                                          ; Required due to user interactions
                                          :clean? true ; Inital state of running not have happened at all. E.g user interaction Clean
                                          :running? false
                                          :finished? false ; Timer has finished (Reached 0). 
                                          :break? false; Tracking break

                                          ;; for calculating break length. Due to lack of duration concept and being able to use, it needs to be stored this way
                                          :value-break-start (.now js/Date) ; Doesn't mutate in length calc logic (although logic can, my design I only modify end.)
                                          :value-break-end (date-fns/addMinutes (.now js/Date) 5)  ;default break of 5 minutes. Gives 4. diff in ms must be rounded down.

                                          ;; for calculating next pomodoro length. 25 by default.
                                          :value-next-start (date-fns/addMinutes (.now js/Date) 0) ; Doesn't mutate in length calc logic (although logic can, my design I only modify end.)
                                          :value-next-end (date-fns/addMinutes (.now js/Date) 25)
                                          ; :value-next-end (date-fns/addSeconds (.now js/Date) 3) ; *** Modify this to change end timer

                                          :pomo-count 0
                                          :dev? (rf/subscribe [:dev?])})

                     alarm-ref (reagent/atom nil)

                     fn-play-alarm (fn [] (when-let [ref @alarm-ref]
                                            (if (.-paused ref)
                                              (.play ref)
                                              (.pause ref))))

                     fn-load-alarm (fn [] (when-let [ref @alarm-ref] ;; not nil?
                                            (.load ref)))

                     ;; CSS
                     css-current-session-text (reagent/atom "invisible")
                     css-next-timer (reagent/atom "invisible")

                     ; With animation baked in
                     animate-css-fade-fn (fn [css-atom length] (fn-animate-css css-atom "animate__fadeIn" "animate__fadeOut" length))

                     timer-id (reagent/atom nil) ;setInterval id

                     ; for freeCodeCamp requirement.
                     ; This is keep going, depending on state do one or another.
                     timer-fn (fn [] (js/setInterval
                                      (fn []
                                            ; Start will always be now. This will cause end - start calculation to change hence countdown (or countup). Based on this change calculation and conditions of app
                                            ; it will cause app to react
                                        (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))

                                            ; When end-start = 0, trigger finished. This could be session or break. Sets next timer. This should only run when time is at 0.
                                        (when (>= 0 (date-fns/differenceInSeconds (get-in @state [:end]) (get-in @state [:start])))
                                          (if (get-in @state [:break?])
                                            ((fn []
                                                   ; Case break timer has reached zero. Need to setup system to star normal session timer.
                                               (swap! state assoc-in [:finished?] false)
                                               (swap! state assoc-in [:break?] false)
                                               (fn-play-alarm)  ; freeCodecamp Requirement. Design decision. I want to trigger this through finished?. However I'll have to convert this to form-3 and sort after html has mounted. This is easier to do without converting.

                                                 ; Set start to 0, end to break (default is 5). Working out difference in minute of what user inserted
                                               (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))
                                               (swap! state assoc-in [:end] (date-fns/addMinutes (.now js/Date)
                                                                                                 (date-fns/differenceInMinutes (get-in @state [:value-next-end]) (get-in @state [:value-next-start]))))
                                               (animate-css-fade-fn css-current-session-text 3000)))

                                            ((fn []
                                                   ; Case finished session
                                               (swap! state assoc-in [:finished?] true)
                                               (swap! state assoc-in [:break?] true)
                                               (fn-play-alarm)

                                                 ; Set start to 0, end to break (default is 5). Working out difference in minute of what user inserted
                                               (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))
                                               (swap! state assoc-in [:end] (date-fns/addMinutes (.now js/Date)
                                                                                                 (date-fns/differenceInMinutes (get-in @state [:value-break-end]) (get-in @state [:value-break-start]))))
                                               (animate-css-fade-fn css-current-session-text 3000)

                                               ; Increase pomodoro count (for icon-array)
                                               (swap! state update-in [:pomo-count] inc))))))
                                      70))

                     ; Resets 
                     fn-reset (fn [e]
                                (let [next-pomo-length (date-fns/differenceInMinutes (get-in @state [:value-next-end]) (get-in @state [:value-next-start]))]
                                  (js/clearInterval @timer-id)
                                  (swap! state assoc-in [:clean?] true)
                                  (swap! state assoc-in [:running?] false)
                                  (swap! state assoc-in [:finished?] false)
                                  (swap! state assoc-in [:break?] false)

                                  (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))  ; This needs to be reset to now.
                                  ;; (swap! state assoc-in [:end] (date-fns/addMinutes (.now js/Date) next-pomo-length)) ;; My preferred logic. Insert this and remove freeCodeCamp Requirement below to revert back.

                                  ;;;;;;;;;;;
                                  ;; freeCodeCamp Requirement (temp  This logic is overriding my architecture "next-pomo-length")


                                  ; Reset pomodoro length timer
                                  (swap! state assoc-in [:value-next-start] (date-fns/addMinutes (.now js/Date) 0))  ; reset next timer to now. This logic is overriding my architecture "next-pomo-length"
                                  (swap! state assoc-in [:value-next-end] (date-fns/addMinutes (.now js/Date) 25))  ; reset next timer to 25 minutes This logic is overriding my architecture "next-pomo-length"
                                  (swap! state assoc-in [:end] (date-fns/addMinutes (.now js/Date) 25))  ; reset (force) next time to be 25 minutes. This logic is overriding my architecture "next-pomo-length"
                                  ;;;;;;;;;;;

                                  ; Reset break timer.
                                  (swap! state assoc-in [:value-break-start] (date-fns/addMinutes (.now js/Date) 0))
                                  (swap! state assoc-in [:value-break-end] (date-fns/addMinutes (.now js/Date) 5))

                                  ; Rewind 'Load' alarm
                                  (fn-load-alarm)

                                  ; Reset Pomodoro count "icon-array". TODO: Disabled for now. Reload the age instead to reset fully
                                  #_(swap! state assoc-in [:pomo-count] 0)))

                     ;;  Initial Start
                     fn-start (fn [e]
                                (let [pomo-next-length (date-fns/differenceInMinutes (get-in @state [:value-next-end]) (get-in @state [:value-next-start]))]
                                  (swap! state assoc-in [:clean?] false)
                                  (swap! state assoc-in [:running?] true)
                                  (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))
                                  (swap! state assoc-in [:end] (date-fns/addMinutes (.now js/Date) pomo-next-length))
                                  (swap! timer-id timer-fn)))

                     ; This function is not callable in button when inappropriate
                     ; Calculation in millisecond as it can be paused at resumed in this scale
                     fn-resume (fn [e]
                                 (let [pomo-left-length (date-fns/differenceInMilliseconds (get-in @state [:end]) (get-in @state [:start]))]
                                   (swap! state assoc-in [:clean?] false)
                                   (swap! state assoc-in [:running?] true)
                                   (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))
                                   (swap! state assoc-in [:end] (date-fns/addMilliseconds (.now js/Date) pomo-left-length))
                                   (animate-css-fade-fn css-current-session-text 1200)
                                   (swap! timer-id timer-fn)))

                     fn-pause (fn [e]
                                (js/clearInterval @timer-id)
                                (swap! state assoc-in [:running?] false))
                     
                     title-atom (reagent/atom nil)]

    (let [; Currently running
          compound-duration-all (diff-in-duration (get-in @state [:end]) (get-in @state [:start])) #_(seconds->duration (date-fns/differenceInSeconds (get-in @state [:end]) (get-in @state [:start])))
          compound-duration-filtered (dissoc compound-duration-all :w :d :h)
          ms (when (get-in @state [:running?]) (mod (date-fns/differenceInMilliseconds (get-in @state [:end]) (get-in @state [:start])) 1000))
          compound-duration-plus-ms (assoc compound-duration-filtered :ms ms)

          ; Next to run
          next-compound-duration-all (diff-in-duration (get-in @state [:value-next-end]) (get-in @state [:value-next-start])) #_(seconds->duration (date-fns/differenceInSeconds (get-in @state [:end]) (get-in @state [:start])))
          next-compound-duration-filtered (dissoc next-compound-duration-all :w :d :h)
          next-ms (when (get-in @state [:running?]) (mod (date-fns/differenceInMilliseconds (get-in @state [:value-next-end]) (get-in @state [:value-next-start])) 1000))
          next-compound-duration-plus-ms (assoc next-compound-duration-filtered :ms next-ms)

          break-length (date-fns/differenceInMinutes (get-in @state [:value-break-end]) (get-in @state [:value-break-start])) ; default 5
          next-pomo-length (date-fns/differenceInMinutes (get-in @state [:value-next-end]) (get-in @state [:value-next-start])) ; default 25
          pomo-left-length (date-fns/differenceInMinutes (get-in @state [:start]) (get-in @state [:end])) ; Current left
          clean? (get-in @state [:clean?])
          running? (get-in @state [:running?])
          finished? (get-in @state [:finished?])
          break? (get-in @state [:break?])

          pomo-count (get-in @state [:pomo-count])

          ; Logic of which component duration to show. 
          display-compound-duration (reagent/atom (cond
                                                    clean? next-compound-duration-plus-ms
                                                    running? compound-duration-plus-ms
                                                    finished? {:m 0 :s 0 :ms 0} ; {:h 0 :m 0 :s 0 :ms 0} Now this doesn't need to be hard coded and actual calculation should give same. Also interval should stop and not countdown below
                                                    :else compound-duration-plus-ms #_{:h 1 :m 2 :s 3 :ms 4}))

          ;; display-compound-duration (cond
          ;;                             clean? next-compound-duration-plus-ms
          ;;                             running? compound-duration-plus-ms
          ;;                             finished? {:h 0 :m 0 :s 0 :ms 0} ; Now this doesn't need to be hard coded and actual calculation should give same. Also interval should stop and not countdown below
          ;;                             :else compound-duration-plus-ms #_{:h 1 :m 2 :s 3 :ms 4})

          ; For clicking logic. Sometimes click should trigger, sometimes not.
          next-timer-animate-fn-logic (if (or running? finished? (not clean?))
                                        (fn [] (animate-css-fade-fn css-next-timer 1200))
                                        (fn [] nil))]

      [:div.flex.flex-col.items-center.justify-center.content-center.self-center
       ; Using centered allows this to stay mostly middle with temporary coming in and out. Possible for mobile this needs media query. Visible
       (let [class-bg @(rf/subscribe [:theme/general-bg 100])
             class-text @(rf/subscribe [:theme/general-text 400])
             hover (str "hover:" class-bg)
             class (join  " " ["mb-3" "rounded" hover class-text])]
         [input/title {:value @title-atom
                       :class class
                       :on-change (fn [e] (reset! title-atom (-> e .-target .-value)))}])
       [:div.opacity-50.centered.animate__animated.animate__faster {:class @css-next-timer}  ; invisible as default stays in DOM if this converts into flex box.
        ;; [clock/digital-clean {:compound-duration {:h 0 :m next-pomo-length :s 0 :ms 0}}]
        [clock/digital-clean {:compound-duration {:m next-pomo-length}}]]
       [:div.opacity-50.centered.text-3xl.animate__animated {:class @css-current-session-text}
        [:p#timer-label.btn.font-normal (if break? "Break" "Session")]]
       [:div.flex.flex-col.items-center.hidden ; HIDDEN. Here for freeCodeCamp Requirement
        [:div#timer-label.btn (if break? "Break" "Session")]
        [:div#session-length.btn (humanize-double-digit (:m next-compound-duration-plus-ms)) #_":" #_(humanize-double-digit (:s next-compound-duration-plus-ms))] ; HIDDEN. Here for freeCodeCamp Requirement
        [:div#time-left.btn (humanize-double-digit (:m @display-compound-duration)) ":" (humanize-double-digit (:s @display-compound-duration))] ; HIDDEN. Here for freeCodeCamp Requirement. User Story #8 25:00 (mm:ss format)
        ]
       [:div {:data-tip "Break Length"}
        [input/number {:value break-length
                       :class "transition-25to100 mb-10"
                       :id-value "break-length"
                       :id-button "break-decrement"  ; freeCodeCamp Requirement
                       :id+button "break-increment"  ; freeCodeCamp Requirement
                       :handle-change (fn [] (swap! state update-in [:value-break-end] (fn [v] (date-fns/subMinutes v 1))))
                       :handle+change (fn [] (swap! state update-in [:value-break-end] (fn [v] (date-fns/addMinutes v 1))))
                       :validation-button (<= break-length 0) ; When 0 or smaller don't let value get smaller.
                       :validation+button (>= break-length 60) ; freeCodeCamp Requirement
                       }]
        [:> ReactTooltip {:place "top"  ; This tooltip can be place anywhere. 
                          :type "light"  ;dark is default
                          :effect "solid"}]
        [:div#break-label.btn.hidden "Break Length"] ; HIDDEN. Here for freeCodeCamp Requirement
        ]

       (when @(get-in @state [:dev?])
         [:div.text-sm.flex.flex-col
          [:div "clean? " (str clean?)]
          [:div "running? " (str running?)]
          [:div "finished? " (str finished?)]
          [:div "compound-duration-plus-ms " compound-duration-plus-ms]
          [:div "next-compound-duration-plus-ms " next-compound-duration-plus-ms]
          [:div "display-compound-duration " @display-compound-duration]])

       [:div.flex.flex-row.-mx-5.sm:mx-0
        [:button#session-decrement.btn.btn-nav.rounded-l-full.rounded-r.self-center.transition-25to100
         (merge {:on-click (fn []
                             (swap! state update-in [:value-next-end] (fn [v] (date-fns/subMinutes v 1)))
                             (next-timer-animate-fn-logic))}
                (when (<= next-pomo-length 1) btn-invalid))
         "-"]
        [:div.flex.flex-row.text-6xl.tracking-wide.leading-none.text-opacity-100.cursor-pointer.select-none.mx-3.sm:mx-12
         {:on-click (fn []
                      (swap! state update-in [:ms-visible?] not)
                      (animate-css-fade-fn css-current-session-text 1200))}
         [clock/digital-clean {:compound-duration @display-compound-duration
                              ;;  :compound-duration display-compound-duration
                               :ms-placement (get-in @state [:ms-placement])
                               :ms-visible? (get-in @state [:ms-visible?])}]
         #_[:div.text-base.tracking-wide.leading-none.text-opacity-100.mt-2 ms]]
        [:button#session-increment.btn.btn-nav.rounded-r-full.rounded-l.self-center.transition-25to100
         (merge {:on-click (fn []
                             (swap! state update-in [:value-next-end] (fn [v] (date-fns/addMinutes v 1)))
                             (next-timer-animate-fn-logic))}
                (when (>= next-pomo-length 59) btn-invalid))  ; freeCodeCamp Requirement
         "+"]]
       (when (get-in @state [:ms?]) [:div.text-base.tracking-wide.leading-none.text-opacity-100.mt-2 ms])
       [:div.flex.flex-row.mt-10.text-xl.transition-25to100.opacity-50
        [:button#reset.btn.btn-nav.mr-8 {:on-click (fn [e] (fn-reset e))} [icon/stop]]
        [:button#start_stop.btn.btn-nav {:on-click (fn [e] (if clean?
                                                             (fn-start e)
                                                             (if running? (fn-pause e) (fn-resume e))))
                                         :disabled finished?
                                         :class (when finished? "cursor-not-allowed opacity-50")}
         (if clean?
           [icon/play]
           (if running? [icon/pause] [icon/play]))]]
       [vis/icon-array {:num pomo-count :class "mt-4 font-normal"}] ; font-bold
       [:audio {:ref (fn [e] (reset! alarm-ref e))
                :id "beep"
                :preload "auto"
                :src "/static/Baoding-balls-ding.mp3"
                :controls false}]
       (when @(get-in @state [:dev?]) [dev-panel [state timer-id alarm-ref]])])
    (finally (js/clearInterval timer-id))))

(defn pomodoro-panel-nav []
  (let [nav-styled? (reagent/atom true)]
    (fn []
      [:div.flex-center.h-full
       [:div.mb-5 (if @nav-styled? [:button.btn.btn-nav {:on-click #(swap! nav-styled? not)} "Advance"] [:button.btn.btn-nav {:on-click #(swap! nav-styled? not)} "Clean"])]
       [:div.flex-center.h-full.w-full (if @nav-styled? [pomodoro-simple--options] [:div [pomodoro-simple]])]])))

(defn pomodoro-page-container []
  [:main [pomodoro-panel-nav]])
