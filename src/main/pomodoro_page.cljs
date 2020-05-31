(ns pomodoro-page
  (:require
   [reagent.core :as reagent]))



(defn countdown-component []
  (reagent/with-let [seconds-left (reagent/atom 60)
                     timer-fn     (js/setInterval #(swap! seconds-left dec) 1000)]
    [:div.timer
     [:div "Time Remaining: " (str @seconds-left)]]
    (finally (js/clearInterval timer-fn))))

(defn pomodoro-panel []
  [:div
   [:h1 "pomodoro panel"]
   [:h2 "countdown-component"]

   [countdown-component]
   ])

(defn pomodoro-page-container []
  [:div [pomodoro-panel]])
