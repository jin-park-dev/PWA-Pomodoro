(ns pomodoro-page
  (:require
   [reagent.core :as reagent]))



(defn pomodoro-panel []
  [:div
   [:h1 "pomodoro panel"]])

(defn pomodoro-page-container []
  [:div
   [:h1 "pomodoro Container"]
   [pomodoro-panel]]
  )
