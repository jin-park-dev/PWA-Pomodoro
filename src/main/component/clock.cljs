(ns component.clock
  (:require [reagent.core :as reagent]
            [stylefy.core :as stylefy :refer [use-style]]
            [date-fns :as date-fns]))


(defn digital-clean
  "Display in simple digital style"
  [{:keys [compound-duration]
    :or   {compound-duration {:h 0 :m 0 :s 0}  ; default if no number
           }}]
  
  [:div.flex.flex-row.text-6xl.tracking-wide.leading-none.text-teal-500.text-opacity-100.cursor-pointer.select-none
;;    {:on-click #(swap! state update-in [:ms?] not)}
   (doall
    (for [[k v] compound-duration]
           ; Change original datastructure to nil to choose when to have number or now ?
      [:div.flex.flex-row.mr-2 {:key k} [:div v] [:div.text-base.self-end k]]))
   #_[:div.text-base.tracking-wide.leading-none.text-teal-500.text-opacity-100.mt-2 ms]])