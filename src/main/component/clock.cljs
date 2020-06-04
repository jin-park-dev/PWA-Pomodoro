(ns component.clock
  (:require [reagent.core :as reagent]
            [stylefy.core :as stylefy :refer [use-style]]
            [date-fns :as date-fns]))


(defn digital-clean
  "Display only - Simple clean digital style
   Example of arguement - {:compound-duration {:h 0 :m 0 :s 0}}
   uses key and value to display.
   placement - right, bottom, nil
   "
  [{:keys [compound-duration placement]
    :or   {compound-duration {:h 0 :m 0 :s 0}  ; default if no number
           placement nil}}]
  (let [_compound-duration (dissoc compound-duration :ms)
        ms (:ms _compound-duration 0)]
    [:div.flex.flex-col.items-center.justify-center.content-center.self-center
     [:div.flex.flex-row.text-6xl.tracking-wide.leading-none.text-teal-500.text-opacity-100.cursor-pointer.select-none
;;    {:on-click #(swap! state update-in [:ms?] not)}
      (doall
       (for [[k v] _compound-duration]
           ; Change original datastructure to nil to choose when to have number or now ?
         [:div.flex.flex-row.mr-2 {:key k} [:div v] [:div.text-base.self-end k]]))
      (when (= placement "right") [:div.text-base.tracking-wide.leading-none.text-teal-500.text-opacity-100.mt-2 ms])]
     (when (= placement "bottom") [:div.text-base.tracking-wide.leading-none.text-teal-500.text-opacity-100.mt-2 ms])]))