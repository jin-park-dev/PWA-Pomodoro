(ns timer-page
  (:require
   [reagent.core :as reagent]
   [stylefy.core :as stylefy :refer [use-style]]
   [date-fns :as date-fns]
   [util.time :refer [seconds->duration]]
   [util.dev :refer [dev-panel]]
   [component.clock :as clock]
   ))


(defn countup-component []
  (reagent/with-let [seconds-left (reagent/atom 60)
                     timer-fn     (js/setInterval #(swap! seconds-left inc) 1000)
                     state {:start nil
                            :now nil}]
    [:div.timer [:div "Time Remaining: " (str @seconds-left)]]
    (finally (js/clearInterval timer-fn))))

; TODO: can't put "compound-duration" above in with-let. Atom seems to not get evalutated so need second let? Or anther way?
; TODO: More control on which unit time when shown. But do I really need days?
(defn timer-simple []
  (reagent/with-let [state (reagent/atom {:start (.now js/Date)
                                          :now (.now js/Date)
                                          :start? false
                                          :ms-visible? false
                                          :ms-placement "bottom"
                                          :dev? false}) ; No button currently for dev?
                     timer-fn     (js/setInterval
                                   #(swap! state assoc-in [:now] (.now js/Date)) 70)]  ;refreshed every 70ms. 1000ms = 1sec
    (let [compound-duration-all (seconds->duration (date-fns/differenceInSeconds (get-in @state [:now]) (get-in @state [:start])))
          compound-duration-filtered (dissoc compound-duration-all :w :d)  ; Remove week, days. (Maybe add back if needed one day but it disables showing those two then.)
          ms (when (get-in @state [:start?]) (mod (date-fns/differenceInMilliseconds (get-in @state [:now]) (get-in @state [:start])) 1000))
          compound-duration-plus-ms (assoc compound-duration-filtered :ms ms)
          compound-duration (if (get-in @state [:start?]) compound-duration-plus-ms {:h 0 :m 0 :s 0 :ms 0})  ; Although component has default explictly choosing when on/off this way.
          ]
      [:div.flex.flex-col.items-center.justify-center.content-center.self-center
       [:div.flex.flex-row.text-6xl.tracking-wide.leading-none.text-teal-500.text-opacity-100.cursor-pointer.select-none
        {:on-click #(swap! state update-in [:ms-visible?] not)}
        [clock/digital-clean {:compound-duration compound-duration
                              :ms-placement (get-in @state [:ms-placement])
                              :ms-visible? (get-in @state [:ms-visible?])}]]
       [:div.flex.flex-row.mt-5.text-xl
        #_[:button.btn.btn-nav.mr-2 {:on-click #(swap! state assoc-in [:start] (.now js/Date))} "Reset"] ;; Pause requires time adding/removing everytime pause is pressed. Lets keep simple for now. so 1 button
        [:button.btn.btn-nav {:on-click (fn [e]
                                          (swap! state update-in [:start?] not)
                                          (swap! state assoc-in [:start] (.now js/Date)))}
         (if (get-in @state [:start?]) "Restart" "Start")]]
       (when (get-in @state [:dev?]) [dev-panel [state]])])
    (finally (js/clearInterval timer-fn))))

(defn timer-panel-nav []
  [:div.flex-center.h-full
   [timer-simple]])

(defn timer-page-container []
  [:main [timer-panel-nav]])


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