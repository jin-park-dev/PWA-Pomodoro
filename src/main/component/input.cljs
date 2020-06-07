(ns component.input
  (:require [reagent.core :as reagent]
            [stylefy.core :as stylefy :refer [use-style]]))


(defn number
  "Display only - Simple clean digital style
   Example of arguement - {:compound-duration {:h 0 :m 0 :s 0}}
   uses key and value to display.
   placement - right, bottom, nil
   "
  [{:keys [value handle-change handle+change class]
    :or   {value 0  ; default if no number
           handle-change (fn [] (js/console.log "handle-change pressed. Please add function"))
           handle+change (fn [] (js/console.log "handle+change pressed. Please add function"))
           class nil}
    }]
  (let []
    [:div.flex.flex-row {:class class}
     [:button#break-decrement.btn.btn-nav {:on-click handle-change}
      "-"]
     [:div#break-length.btn.btn-nav.mx-4 value]
     [:button#break-increment.btn.btn-nav {:on-click handle+change}
      "+"]]))
