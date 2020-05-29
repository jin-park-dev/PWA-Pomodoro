(ns index-page
  (:require
   [reagent.core :as reagent]))



(defn index-panel []
  [:div
   [:h1 "index panel"]
   ])

(defn index-page-container []
  [:div
   [:h1 "index Container"]
   [index-panel]]
  )
