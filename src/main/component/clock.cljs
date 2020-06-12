(ns component.clock
  (:require [reagent.core :as reagent]
            [stylefy.core :as stylefy :refer [use-style]]
            [date-fns :as date-fns]
            [util.time :refer [humanize-double-digit]]))

; TOOD: All time components - ms-placement "right", due to flexbox rule will keep adjusting, aka timer shaking right and left. Using bottom only for now.

;; .text-6xl.tracking-wide.leading-none.text-teal-500.text-opacity-100
(defn digital-clean
  "Clean style clock. Logic free. Container above should contain what time-now comes in here.
   Takes jsDate"
  [{:keys [time-now date-visible? ms-placement ms-visible? class]
    :or   {time-now (.now js/Date)  ; default of now, Not moving?
           date-visible? false
           ms-placement "bottom"
           ms-visible? false
           class nil}}]
  (let [_ms-placement (when ms-visible? ms-placement)]
    [:div.flex.flex-col.items-center.justify-center.content-center.self-center {:class class}
     (when date-visible? [:div.text-xl (date-fns/format time-now "y-MM-dd iii")])
     [:div.flex.flex-row.my-5
      [:div (date-fns/format time-now "h")]
      [:div ":"]
      [:div (date-fns/format time-now "mm")]
      [:div ":"]
      [:div.mr-5 (date-fns/format time-now "ss")]
      [:div (date-fns/format time-now "aaa")]
      (when (= _ms-placement "right") [:div.text-base (date-fns/format time-now "SSS")])]
     (when (= _ms-placement "bottom") [:div.text-base (date-fns/format time-now "SSS")])]))
  
    


#_(defn digital-clean
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
       [:div.flex.flex-row.text-6xl.tracking-wide.leading-none.text-teal-500.text-opacity-100.cursor-pointer.select-none
        (doall
         (for [[k v] _compound-duration]
           ; Change original datastructure to nil to choose when to have number or now ?
           [:div.flex.flex-row.mr-2 {:key k} [:div v] [:div.text-base.self-end k]]))
        (when (= _ms-placement "right") [:div.text-base.tracking-wide.leading-none.text-teal-500.text-opacity-100.mt-2 ms])]
       (when (= _ms-placement "bottom") [:div.text-base.tracking-wide.leading-none.text-teal-500.text-opacity-100.mt-2 ms])]))