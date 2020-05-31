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

(defn timer-simple []
  (reagent/with-let [seconds-left (reagent/atom 60)
                     timer-fn     (js/setInterval #(swap! seconds-left inc) 1000)
                     state {:start nil
                            :now nil}]
    [:div
     [:div "Time Remaining: " (str @seconds-left)]
     [:div.flex.flex-row
      [:button.btn.btn-nav.mr-2 "Reset"]
      [:button.btn.btn-nav "Start"]]
     ]
    (finally (js/clearInterval timer-fn)))
  )

(defn timer-panel []
  [:div 
   [timer-simple]])

(defn timer-page-container []
  [:div [timer-panel]])
