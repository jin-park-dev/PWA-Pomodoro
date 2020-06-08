(ns pomodoro-page
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [state.subs :as sub] ; Needed for subs
   [stylefy.core :as stylefy :refer [use-style]]
   [date-fns :as date-fns]
   ["react-tooltip" :as ReactTooltip]
   [util.time :refer [seconds->duration diff-in-duration]]
   [util.dev :refer [dev-panel]]
   [component.clock :as clock]
   [component.input :as input]))


(defn pomodoro-simple []
  (reagent/with-let [state (reagent/atom {:start (.now js/Date)
                                          :now (date-fns/addMinutes (.now js/Date) 25)
                                          ;; :now (date-fns/addSeconds (.now js/Date) 5)
                                          :ms-visible? false
                                          :ms-placement "bottom"

                                          :start? false
                                          :finished? false
                                          :dev? (rf/subscribe [:dev?])}) ; Seems not reactive if I destructure here
                     
                     timer-id (reagent/atom nil) ;setInterval id used to clear interval
                     
                     timer-fn     (fn [] (js/setInterval
                                          (fn []
                                            (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0) #_(.now js/Date))
                                            (when (= 0 (date-fns/differenceInSeconds (get-in @state [:now]) (get-in @state [:start])))
                                              (swap! state assoc-in [:finished?] true)))
                                          70))
                     ]
    (let [compound-duration-all (diff-in-duration (get-in @state [:now]) (get-in @state [:start]))
          compound-duration-filtered (dissoc compound-duration-all :w :d)
          ms (when (get-in @state [:start?]) (mod (date-fns/differenceInMilliseconds (get-in @state [:now]) (get-in @state [:start])) 1000))
          compound-duration-plus-ms (assoc compound-duration-filtered :ms ms)
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
                                          (swap! state assoc-in [:now] (date-fns/addMinutes (.now js/Date) 25)))}
         (if (get-in @state [:start?]) "Stop" "Start")]]
       (when @(get-in @state [:dev?]) [dev-panel [state timer-id]])])
    (finally (js/clearInterval @timer-id))))

; date-fns unlike moments does not have duration I can use. I have to caclculate time to get any duration
; This mut be done since js interval are not safe and will go out of sync
(defn pomodoro-simple--options []
  (reagent/with-let [state (reagent/atom {; This will mutate to keep track of the time.
                                          :start (date-fns/addMinutes (.now js/Date) 0)
                                          :now (date-fns/addSeconds (.now js/Date) 5)
                                          ;; :now (date-fns/addMinutes (.now js/Date) 25)

                                          :ms-visible? false
                                          :ms-placement "bottom"  ; bottom, right, nil

                                          :start? false
                                          :finished? false

                                          ;; for calculating break length. Due to lack of duration concept and being able to use, it needs to be stored this way
                                          :value-break-start (.now js/Date) ; Doesn't mutate in length calc logic (although logic can, my design I only modify end.)
                                          :value-break-end (date-fns/addMinutes (.now js/Date) 5)  ;default break of 5 minutes. Gives 4. diff in ms must be rounded down.

                                          ;; for calculating next pomodoro length. 25 by default.
                                          :value-next-start (.now js/Date) ; Doesn't mutate in length calc logic (although logic can, my design I only modify end.)
                                          :value-next-end (date-fns/addMinutes (.now js/Date) 25)
                                          ;; :value-break-end (date-fns/addSeconds (.now js/Date) (+ 1 (* 5 60)))  ;default break of 5 minutes. Gives 4. diff in ms must be rounded down.
                                          ;; :value-break (date-fns/add (.now js/Date) {:minutes 5 :seconds 1})  ;default break of 5 minutes
                                          ;; :value-

                                          :dev? (rf/subscribe [:dev?])}) ; Seems not reactive if I destructure here
                     
                     timer-id (reagent/atom nil) ;setInterval id
                                          
                     timer-fn     (fn [] (js/setInterval
                                              (fn []
                                                (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))
                                                (when (= 0 (date-fns/differenceInSeconds (get-in @state [:now]) (get-in @state [:start])))
                                                  (swap! state assoc-in [:finished?] true)))
                                              70))
                     fn-reset (fn [e]
                                (js/clearInterval @timer-id)
                                (swap! state assoc-in [:start?] false)
                                (swap! state assoc-in [:finished?] false)
                                (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))
                                (swap! state assoc-in [:now] (date-fns/addMinutes (.now js/Date) 25)))
                     
                     fn-start (fn [e next-pomo-length]
                                (swap! state update-in [:start?] not)
                                (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))
                                (swap! state assoc-in [:now] (date-fns/addMinutes (.now js/Date) next-pomo-length))
                                #_(swap! state assoc-in [:now] (date-fns/addSeconds (.now js/Date) 2))
                                (swap! timer-id timer-fn))

                     ]
    (let [compound-duration-all (diff-in-duration (get-in @state [:now]) (get-in @state [:start])) #_(seconds->duration (date-fns/differenceInSeconds (get-in @state [:now]) (get-in @state [:start])))
          compound-duration-filtered (dissoc compound-duration-all :w :d)
          ms (when (get-in @state [:start?]) (mod (date-fns/differenceInMilliseconds (get-in @state [:now]) (get-in @state [:start])) 1000))
          compound-duration-plus-ms (assoc compound-duration-filtered :ms ms)
          compound-duration (cond
                              (get-in @state [:finished?]) {:h 0 :m 0 :s 0 :ms 0}
                              (get-in @state [:start?]) compound-duration-plus-ms
                              :else compound-duration-plus-ms #_{:h 1 :m 2 :s 3 :ms 4})
          break-length (date-fns/differenceInMinutes (get-in @state [:value-break-end]) (get-in @state [:value-break-start])) ; default 5
          next-pomo-length (date-fns/differenceInMinutes (get-in @state [:value-next-end]) (get-in @state [:value-next-start])) ; default 25
          finished? (get-in @state [:finished?])
          ]
      ;; (when (< value-break 0) (swap! state update-in [:value-break-end] (.now js/Date)))
      ;; (println (get-in @state [:start]))
      ;; (println (get-in @state [:now]))
      ;; (println (get-in @state [:value-break]))
      ;; (println "value-break-diff: " value-break)
      [:div.flex.flex-col.items-center.justify-center.content-center.self-center
       [:div.flex
        [:div#timer-label.btn.hidden "Session"] ; HIDDEN. Here for freeCodeCamp Requirement
        [:div#session-length.btn.hidden "25"] ; TODO: Get value from state     HIDDEN. Here for freeCodeCamp Requirement
        [:div#time-left.btn.hidden "25:00 (mm:ss format)"] ; TODO: Get value from state     HIDDEN. Here for freeCodeCamp Requirement
        ]
       [:div {:data-tip "Break Length"}
        [input/number {:value break-length
                       :class "transition-25to100 mb-10"
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

       [:div.flex.flex-row
        [:button#session-decrement.btn.btn-nav.rounded-l-full.rounded-r.self-center.transition-25to100
         {:on-click (fn [] (swap! state update-in [:value-next-end] (fn [v] (date-fns/subMinutes v 1))))}
         "-"]
        [:div.flex.flex-row.text-6xl.tracking-wide.leading-none.text-teal-500.text-opacity-100.cursor-pointer.select-none.mx-12
         {:on-click #(swap! state update-in [:ms-visible?] not)}
         [clock/digital-clean {:compound-duration compound-duration
                               :ms-placement (get-in @state [:ms-placement])
                               :ms-visible? (get-in @state [:ms-visible?])}]
         #_[:div.text-base.tracking-wide.leading-none.text-teal-500.text-opacity-100.mt-2 ms]]
        [:button#session-increment.btn.btn-nav.rounded-r-full.rounded-l.self-center.transition-25to100
         {:on-click (fn [] (swap! state update-in [:value-next-end] (fn [v] (date-fns/addMinutes v 1))))}
         "+"]]
       (when (get-in @state [:ms?]) [:div.text-base.tracking-wide.leading-none.text-teal-500.text-opacity-100.mt-2 ms])
       [:div.flex.flex-row.mt-10.text-xl.transition-25to100.opacity-50
        [:button#reset.btn.btn-nav.mr-8 {:on-click (fn [e] (fn-reset e))
                                         
                                         #_(fn [e]
                                                     (js/clearInterval @timer-id)
                                                     (swap! state assoc-in [:start?] false)
                                                     (swap! state assoc-in [:finished?] false)
                                                     (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))
                                                     (swap! state assoc-in [:now] (date-fns/addMinutes (.now js/Date) 25)))}
         "Reset"]
        [:button#start_stop.btn.btn-nav {:on-click (fn [e] (fn-start e next-pomo-length))
                                         
                                         #_(fn [e]
                                                     (swap! state update-in [:start?] not)
                                                     (swap! state assoc-in [:start] (date-fns/addMinutes (.now js/Date) 0))
                                                     (swap! state assoc-in [:now] (date-fns/addMinutes (.now js/Date) next-pomo-length))
                                                     #_(swap! state assoc-in [:now] (date-fns/addSeconds (.now js/Date) 2))
                                                     (swap! timer-id timer-fn))
                                         :disabled finished?
                                         :class (when finished? "cursor-not-allowed opacity-50")
                                         }
         (if (get-in @state [:start?]) "Stop" "Start")]]
       #_[:button.btn.btn-nav.mt-2 {:on-click (fn [e]
                                                (rf/dispatch [:dev/dev-switch]))}
          "dev: " (pr-str @(rf/subscribe [:dev?]))]
       (when @(get-in @state [:dev?]) [dev-panel [state timer-id]])])
    #_(finally (js/clearInterval timer-fn))
    (finally (js/clearInterval timer-id))
    ))

(defn pomodoro-panel-nav []
  (let [nav-styled? (reagent/atom true)]
    (fn []
      [:div.flex-center.h-full
       [:div.mb-5 (if @nav-styled? [:button.btn.btn-nav {:on-click #(swap! nav-styled? not)} "adv"] [:button.btn.btn-nav {:on-click #(swap! nav-styled? not)} "Clean"])]
       [:div.flex-center.h-full.w-full (if @nav-styled? [pomodoro-simple--options] [:div [pomodoro-simple]])]])))

(defn pomodoro-page-container []
  [:main [pomodoro-panel-nav]])
