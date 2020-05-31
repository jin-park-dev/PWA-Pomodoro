(ns timer-page
  (:require
   [reagent.core :as reagent]
   [stylefy.core :as stylefy :refer [use-style]]
   [date-fns :as date-fns]
   [util.time :refer [seconds->duration]]
   ))

; Temp
; Better way to get iteration number. Maybe merge in at start?
(defn dev-panel [states]
  "Vector of atoms (state)"
  [:div.bg-teal-200.shadow.p-4.mt-5
   [:h1.text-4xl.font-bold.mb-2 "Dev-panel"]
   #_[:div
    [:h2.text-3xl.mb-2 "state"]
    [:div
     (pr-str @states)]]
   (doall
    (for [state states
          :let [counter (atom 0)]]
      [:div.mt-4 {:key (gensym)}
       [:h2.text-3xl.mb-2 "state"]
       [:div
        (pr-str @state)]]))
   ])

; date-fns doesn't have way to convert duration to hour:min:second  !!! (Moment does)
; I'll have to do calcuation by hand. It will show difference in hour, second or millisecond etc
(defn seconds-to-hoursMinutesSeconds [seconds]
  (println "sec: " (mod seconds 60))
  (println "min: " (if (< second 3600) (quot seconds 60) (mod seconds 3600)))
  (println "hour: " (quot seconds 60))
  (println "day: " (quot seconds 60))
  )

; I need start time
; Time now
; do Diff to correct/use to show time diff every... x second (500ms?)
(defn countup-component []
  (reagent/with-let [seconds-left (reagent/atom 60)
                     timer-fn     (js/setInterval #(swap! seconds-left inc) 1000)
                     state {:start nil
                            :now nil}]
    [:div.timer [:div "Time Remaining: " (str @seconds-left)]]
    (finally (js/clearInterval timer-fn))))

; TODO: can't put "compound-duration" above in with-let. Atom seems to not get evalutated so need second let? Or anther way?
; TODO: More control on which unit time when shown
(defn timer-simple []
  (reagent/with-let [state (reagent/atom {:start (.now js/Date)
                                          :now (.now js/Date)})
                     timer-fn     (js/setInterval
                                   #(swap! state assoc-in [:now] (.now js/Date)) 70)]  ;refreshed every 70ms. 1000ms = 1sec
    (let [compound-duration-all (seconds->duration (date-fns/differenceInSeconds (get-in @state [:now]) (get-in @state [:start])))
          compound-duration (dissoc compound-duration-all :w :d)
          ms (mod (date-fns/differenceInMilliseconds (get-in @state [:now]) (get-in @state [:start])) 1000)]
      [:div.flex.flex-col.items-center ;.justify-center.content-center.self-center
       [:div.flex.flex-row.text-6xl.tracking-wide.leading-none.text-teal-500.text-opacity-100
        (doall
         (for [[k v] compound-duration]
           ; Change original datastructure to nil to choose when to have number or now ?
           [:div.flex.flex-row.mr-2 {:key k} [:div v] [:div.text-base.self-end k]]))
        ; [:div ms]
        ]
       [:div.flex.flex-row.mt-2
        [:button.btn.btn-nav.mr-2 {:on-click #(swap! state assoc-in [:start] (.now js/Date))} "Reset"]
        [:button.btn.btn-nav {:on-click #(reset! state not)} "Start"]]
       #_[dev-panel [state]]])
    (finally (js/clearInterval timer-fn))))

(defn timer-panel-nav []
  [:div.mt-56
   [timer-simple]])

(defn timer-page-container []
  [:div [timer-panel-nav]])


(comment
  ; https://cljs.github.io/api/cljs.core/clj-GTjs
  (clj->js {"foo" 1 "bar" 2})
  (clj->js {:foo 1 :bar 2})
  (clj->js [:foo "bar" 'baz])
  (clj->js [1 {:foo "bar"} 4])
  
  (.stringify js/JSON (clj->js {:key "value"}))
  (.stringify js/JSON (clj->js (clj->js {"foo" 1 "bar" 2})))
  (.stringify js/JSON (clj->js (clj->js {:foo 1 :bar 2})))
  
  (js/console.log (clj->js {:foo 1 :bar 2})) ; So this does what I want! JS object json.
  
  (date-fns/formatDuration (clj->js {:months 1 :days 2}))
  (date-fns/formatDuration (clj->js {:seconds 55}))
  (date-fns/formatDuration (clj->js {:seconds 5555}))
  
  (date-fns/formatDuration (clj->js {:seconds 5555}) ["minutes" "seconds"])
  (date-fns/formatDuration (clj->js {:minutes 22 :seconds 5555}) ["minutes" "seconds"])
  
  (date-fns/formatDistance (.now js/Date) (.now js/Date))
  (date-fns/formatDistanceStrict (.now js/Date) (.now js/Date) (clj->js {:unit "minute"}))
  (date-fns/formatDistanceStrict (date-fns/addSeconds (.now js/Date) 123213) (.now js/Date) (clj->js {:unit "minute"}))
  (date-fns/formatDistanceStrict (date-fns/addSeconds (.now js/Date) 123213) (.now js/Date) (clj->js {:unit "second"}))
  
  (seconds->duration 56)
  (seconds->duration 60)
  (seconds->duration 61)
  (seconds->duration 3605)
  )