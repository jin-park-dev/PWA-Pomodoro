(ns timer-page
  (:require
   [reagent.core :as reagent]
   [stylefy.core :as stylefy :refer [use-style]]
   [date-fns :as date-fns]))


; I need start time
; Time now
; do Diff to correct/use to show time diff every... x second (500ms?)
(defn countup-component []
  (reagent/with-let [seconds-left (reagent/atom 60)
                     timer-fn     (js/setInterval #(swap! seconds-left inc) 1000)
                     state {:start nil
                            :now nil}]
    [:div.timer
     [:div "Time Remaining: " (str @seconds-left)]]
    (finally (js/clearInterval timer-fn))))

(defn timer-panel []
  [:div
   [:h1 "timer panel"]
   [:h2 "countup-component"]

   [countup-component]
   [:div]
   [:div]])

(defn timer-page-container []
  [:div
   [:h1 "timer Container"]
   [timer-panel]])
