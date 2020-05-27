(ns clock-page
  (:require
   [reagent.core :as reagent]))



(defn clock-panel []
  [:div
   [:h1 "clock panel"]])

(defn clock-page-container []
  [:div
   [:h1 "clock Container"]
   [clock-panel]]
  )
