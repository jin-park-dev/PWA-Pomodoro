(ns pomodoro-page
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [state.subs :as sub] ; Needed for subs
   [stylefy.core :as stylefy :refer [use-style]]
   [date-fns :as date-fns]
   ["react-tooltip" :as ReactTooltip]
   [util.time :refer [seconds->duration diff-in-duration humanize-double-digit]]
   [util.dev :refer [dev-panel]]
   [component.clock :as clock]
   [component.input :as input]
   [component.style :refer [btn-invalid]]))


(defn pomodoro-simple []
  (reagent/with-let [state (reagent/atom {:start (.now js/Date)
                                          :end (date-fns/addMinutes (.now js/Date) 25)
                                          ;; :end (date-fns/addSeconds (.now js/Date) 5)
                                          :ms-visible? false
                                          :ms-placement "bottom"

                                          :start? false
                                          :finished? false
                                          :dev? (rf/subscribe [:dev?])}) ; Seems not reactive if I destructure here

                     timer-id (reagent/atom nil) ;setInterval id used to clear interval

                     timer-fn     (fn [] (js/setInterval
                                          (fn []
                                            (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0) #_(.now js/Date))
                                            (when (= 0 (date-fns/differenceInSeconds (get-in @state [:end]) (get-in @state [:start])))
                                              (swap! state assoc-in [:finished?] true)))
                                          70))]
    (let [compound-duration-all (diff-in-duration (get-in @state [:end]) (get-in @state [:start]))
          compound-duration-filtered (dissoc compound-duration-all :w :d)
          ms (when (get-in @state [:start?]) (mod (date-fns/differenceInMilliseconds (get-in @state [:end]) (get-in @state [:start])) 1000))
          compound-duration-plus-ms (assoc compound-duration-filtered :ms ms)
          ; compound-duration contains logic for what shows up inside of main timer.
          compound-duration (cond
                              (get-in @state [:finished?]) {:h 0 :m 0 :s 0 :ms 0}
                              (get-in @state [:start?]) compound-duration-plus-ms
                              :else compound-duration-plus-ms)]
      [:div.flex.flex-col.items-center.justify-center.content-center.self-center
       [:div.flex.flex-row.text-6xl.tracking-wide.leading-none.text-teal-500.text-opacity-100.cursor-pointer.select-none
        {:on-click #(swap! state update-in [:ms-visible?] not)}
        [clock/digital-clean {:compound-duration compound-duration
                              :ms-placement (get-in @state [:ms-placement])
                              :ms-visible? (get-in @state [:ms-visible?])}]
        #_[:div.text-base.tracking-wide.leading-none.text-teal-500.text-opacity-100.mt-2 ms]] ; I like side but it keeps changing, due to flex being responsive and fontsize being different. Maybe float or span?
       (when (get-in @state [:ms?]) [:div.text-base.tracking-wide.leading-none.text-teal-500.text-opacity-100.mt-2 ms])
       [:div.flex.flex-row.mt-5.text-xl
        #_[:button.btn.btn-nav.mr-2 {:on-click (fn [e]
                                                 (rf/dispatch [:dev/dev-switch]))}
           "dev: " (pr-str @(rf/subscribe [:dev?]))]
        [:button.btn.btn-nav {:on-click (fn [e]
                                          (cond (get-in @state [:start?]) (js/clearInterval @timer-id) ; Stop Timer
                                                (not (get-in @state [:start?])) (swap! timer-id timer-fn)) ; Start timer
                                          (println "on-click after cond")
                                          (swap! state update-in [:start?] not)
                                          (swap! state assoc-in [:finished?] false)
                                          (swap! state assoc-in [:start] (.now js/Date))
                                          (swap! state assoc-in [:end] (date-fns/addMinutes (.now js/Date) 25)))}
         (if (get-in @state [:start?]) "Stop" "Start")]]
       (when @(get-in @state [:dev?]) [dev-panel [state timer-id]])])
    (finally (js/clearInterval @timer-id))))

; date-fns unlike moments does not have duration I can use. I have to caclculate time to get any duration
; This mut be done since js interval are not safe and will go out of sync
(defn pomodoro-simple--options []
  (reagent/with-let [state (reagent/atom {; This will mutate to keep track of the time.
                                          :start (date-fns/addMinutes (.now js/Date) 0)
                                          :end (date-fns/addMinutes (.now js/Date) 25)

                                          :ms-visible? false
                                          :ms-placement "bottom"  ; bottom, right, nil

                                          :clean? true ; Inital state of running not have happened at all. E.g user interaction Clean
                                          :running? false ; Timer running or not running
                                          :finished? false ; Timer has finished (Reached 0). 
                                          :break? false; Tracking break

                                          ;; for calculating break length. Due to lack of duration concept and being able to use, it needs to be stored this way
                                          :value-break-start (.now js/Date) ; Doesn't mutate in length calc logic (although logic can, my design I only modify end.)
                                          :value-break-end (date-fns/addMinutes (.now js/Date) 5)  ;default break of 5 minutes. Gives 4. diff in ms must be rounded down.

                                          ;; for calculating next pomodoro length. 25 by default.
                                          :value-next-start (date-fns/addMinutes (.now js/Date) 0) ; Doesn't mutate in length calc logic (although logic can, my design I only modify end.)
                                          :value-next-end (date-fns/addMinutes (.now js/Date) 25)
                                          ;; :value-break-end (date-fns/addSeconds (.now js/Date) (+ 1 (* 5 60)))  ;default break of 5 minutes. Gives 4. diff in ms must be rounded down.
                                          ;; :value-break (date-fns/add (.now js/Date) {:minutes 5 :seconds 1})  ;default break of 5 minutes
                                          ;; :value-

                                          :dev? (rf/subscribe [:dev?])}) ; Seems not reactive if I destructure here

                     
                     css-current-session-text (reagent/atom "invisible")
                     css-next-timer (reagent/atom "invisible")
                     
                     animate-css-fade-fn (fn [css-atom length]
                                             (reset! css-atom "animate__fadeIn")
                                             (js/setTimeout (fn [] (reset! css-atom "animate__fadeOut")) length)
                                             )
                     
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

                                                 ; Set start to 0, end to break (default is 5). Working out difference in minute of what user inserted
                                                   (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))
                                                   (swap! state assoc-in [:end] (date-fns/addMinutes (.now js/Date)
                                                                                                     (date-fns/differenceInMinutes (get-in @state [:value-next-end]) (get-in @state [:value-next-start]))))
                                                   (animate-css-fade-fn css-current-session-text 3000)))
                                                
                                                ((fn []
                                                   ; Case finished session
                                                   (swap! state assoc-in [:finished?] true)
                                                   (swap! state assoc-in [:break?] true)


                                                 ; Set start to 0, end to break (default is 5). Working out difference in minute of what user inserted
                                                   (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))
                                                   (swap! state assoc-in [:end] (date-fns/addMinutes (.now js/Date)
                                                                                                     (date-fns/differenceInMinutes (get-in @state [:value-break-end]) (get-in @state [:value-break-start]))))
                                                   (animate-css-fade-fn css-current-session-text 3000)))))
                                            )
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
                                  (swap! state assoc-in [:value-break-end] (date-fns/addMinutes (.now js/Date) 5))))

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
                                   (swap! timer-id timer-fn)))

                     fn-pause (fn [e]
                                (js/clearInterval @timer-id)
                                (swap! state assoc-in [:running?] false))]

    (let [; Currently running
          compound-duration-all (diff-in-duration (get-in @state [:end]) (get-in @state [:start])) #_(seconds->duration (date-fns/differenceInSeconds (get-in @state [:end]) (get-in @state [:start])))
          compound-duration-filtered (dissoc compound-duration-all :w :d)
          ms (when (get-in @state [:running?]) (mod (date-fns/differenceInMilliseconds (get-in @state [:end]) (get-in @state [:start])) 1000))
          compound-duration-plus-ms (assoc compound-duration-filtered :ms ms)

          ; Next to run
          next-compound-duration-all (diff-in-duration (get-in @state [:value-next-end]) (get-in @state [:value-next-start])) #_(seconds->duration (date-fns/differenceInSeconds (get-in @state [:end]) (get-in @state [:start])))
          next-compound-duration-filtered (dissoc next-compound-duration-all :w :d)
          next-ms (when (get-in @state [:running?]) (mod (date-fns/differenceInMilliseconds (get-in @state [:value-next-end]) (get-in @state [:value-next-start])) 1000))
          next-compound-duration-plus-ms (assoc next-compound-duration-filtered :ms next-ms)

          break-length (date-fns/differenceInMinutes (get-in @state [:value-break-end]) (get-in @state [:value-break-start])) ; default 5
          next-pomo-length (date-fns/differenceInMinutes (get-in @state [:value-next-end]) (get-in @state [:value-next-start])) ; default 25
          pomo-left-length (date-fns/differenceInMinutes (get-in @state [:start]) (get-in @state [:end])) ; Current left
          clean? (get-in @state [:clean?])
          running? (get-in @state [:running?])
          finished? (get-in @state [:finished?])
          break? (get-in @state [:break?])

          ; Logic of which component duration to show. 
          display-compound-duration (reagent/atom (cond
                                                    clean? next-compound-duration-plus-ms
                                                    running? compound-duration-plus-ms
                                                    finished? {:h 0 :m 0 :s 0 :ms 0} ; Now this doesn't need to be hard coded and actual calculation should give same. Also interval should stop and not countdown below
                                                    :else compound-duration-plus-ms #_{:h 1 :m 2 :s 3 :ms 4}))

          ;; display-compound-duration (cond
          ;;                             clean? next-compound-duration-plus-ms
          ;;                             running? compound-duration-plus-ms
          ;;                             finished? {:h 0 :m 0 :s 0 :ms 0} ; Now this doesn't need to be hard coded and actual calculation should give same. Also interval should stop and not countdown below
          ;;                             :else compound-duration-plus-ms #_{:h 1 :m 2 :s 3 :ms 4})

          ; For clicking logic. Sometimes click should trigger, sometimes not.
          next-timer-animate-fn-logic (if (or running? finished? (not clean?))
                                        (fn [] (animate-css-fade-fn css-next-timer 1200))
                                        (fn [] nil))
          ]
      [:div.flex.flex-col.items-center.justify-center.content-center.self-center
       ; Using centered allows this to stay mostly middle with temporary coming in and out. Possible for mobile this needs media query. Visible
       [:div.opacity-50.centered.animate__animated.animate__faster {:class @css-next-timer}  ; invisible as default stays in DOM if this converts into flex box.
        ;; [clock/digital-clean {:compound-duration {:h 0 :m next-pomo-length :s 0 :ms 0}}]
        [clock/digital-clean {:compound-duration {:m next-pomo-length}}]]
       [:div.opacity-50.centered.text-3xl.text-gray-700.animate__animated {:class @css-current-session-text}
        [:div#timer-label.btn (if break? "Break" "Session")]]
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

       [:div.flex.flex-row
        [:button#session-decrement.btn.btn-nav.rounded-l-full.rounded-r.self-center.transition-25to100
         (merge {:on-click (fn []
                             (swap! state update-in [:value-next-end] (fn [v] (date-fns/subMinutes v 1)))
                             (next-timer-animate-fn-logic))}
                (when (<= next-pomo-length 1) btn-invalid))
         "-"]
        [:div.flex.flex-row.text-6xl.tracking-wide.leading-none.text-teal-500.text-opacity-100.cursor-pointer.select-none.mx-12
         {:on-click (fn [] 
                      (swap! state update-in [:ms-visible?] not)
                      (animate-css-fade-fn css-current-session-text 1200)
                      )}
         [clock/digital-clean {:compound-duration @display-compound-duration
                              ;;  :compound-duration display-compound-duration
                               :ms-placement (get-in @state [:ms-placement])
                               :ms-visible? (get-in @state [:ms-visible?])}]
         #_[:div.text-base.tracking-wide.leading-none.text-teal-500.text-opacity-100.mt-2 ms]]
        [:button#session-increment.btn.btn-nav.rounded-r-full.rounded-l.self-center.transition-25to100
         (merge {:on-click (fn []
                             (swap! state update-in [:value-next-end] (fn [v] (date-fns/addMinutes v 1)))
                             (next-timer-animate-fn-logic))}
                (when (>= next-pomo-length 59) btn-invalid))  ; freeCodeCamp Requirement
         "+"]]
       (when (get-in @state [:ms?]) [:div.text-base.tracking-wide.leading-none.text-teal-500.text-opacity-100.mt-2 ms])
       [:div.flex.flex-row.mt-10.text-xl.transition-25to100.opacity-50
        [:button#reset.btn.btn-nav.mr-8 {:on-click (fn [e] (fn-reset e))}
         "Reset"]
        [:button#start_stop.btn.btn-nav {:on-click (fn [e] (if clean?
                                                             (fn-start e)
                                                             (if running? (fn-pause e) (fn-resume e))))
                                         :disabled finished?
                                         :class (when finished? "cursor-not-allowed opacity-50")}
         (if clean?
           "Start"
           (if running? "Pause" "Resume"))]]
       #_[:button.btn.btn-nav.mt-2 {:on-click (fn [e]
                                                (rf/dispatch [:dev/dev-switch]))}
          "dev: " (pr-str @(rf/subscribe [:dev?]))]
       (when @(get-in @state [:dev?]) [dev-panel [state timer-id]])])
    (finally (js/clearInterval timer-id))))

(defn pomodoro-panel-nav []
  (let [nav-styled? (reagent/atom true)]
    (fn []
      [:div.flex-center.h-full
       [:div.mb-5 (if @nav-styled? [:button.btn.btn-nav {:on-click #(swap! nav-styled? not)} "Advance"] [:button.btn.btn-nav {:on-click #(swap! nav-styled? not)} "Clean"])]
       [:div.flex-center.h-full.w-full (if @nav-styled? [pomodoro-simple--options] [:div [pomodoro-simple]])]])))

(defn pomodoro-page-container []
  [:main [pomodoro-panel-nav]])
