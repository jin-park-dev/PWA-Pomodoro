(ns component.timer
  (:require [reagent.core :as reagent]
            [util.time :refer [humanize-double-digit]]))


(defn digital-clean
  "Display only - Simple clean digital style
   Example of arguement - {:compound-duration {:h 0 :m 0 :s 0}}
   uses key and value to display.
   placement - right, bottom, nil
   "
  [{:keys [compound-duration ms-placement ms-visible?]
    :or   {compound-duration {:h 0 :m 0 :s "00" :ms 0}  ; default if no number
           ms-placement "bottom"
           ms-visible? false}}]
  (let [_compound-duration-filtered (dissoc compound-duration :ms)
        _compound-duration (if (nil? (:s compound-duration))  ; If there is second, humanize it.
                             _compound-duration-filtered
                             (update _compound-duration-filtered :s humanize-double-digit))
        ms (:ms compound-duration 0)
        _ms-placement (when ms-visible? ms-placement)]
    [:div.flex.flex-col.items-center.justify-center.content-center.self-center
     [:div.flex.flex-row.text-6xl.tracking-wide.leading-none.text-opacity-100.cursor-pointer.select-none
      (doall
       (for [[k v] _compound-duration]
           ; Change original datastructure to nil to choose when to have number or now ?
         [:div.flex.flex-row.mr-2 {:key k} [:div v] [:div.text-base.self-end k]]))
      (when (= _ms-placement "right") [:div.text-base.tracking-wide.leading-none.text-opacity-100.mt-2 ms])]
     (when (= _ms-placement "bottom") [:div.text-base.tracking-wide.leading-none.text-opacity-100.mt-2 ms])]))


(defn digital-clean-text
  "No style"
  [{:keys [compound-duration]}]
  (let [_compound-duration-filtered (dissoc compound-duration :ms)
        _compound-duration _compound-duration-filtered]
    [:div.inline-flex.flex-row
     (doall
      (for [[k v] _compound-duration]
        (when-not (= 0 v) [:div.flex.flex-row.mr-2 {:key k} [:div v] [:div.text-base.self-end k]])))]))