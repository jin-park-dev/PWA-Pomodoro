(ns state.index
  (:require
   [re-frame.core :as rf]))

; Trying different way of keeping event and subscription together. Based on page over events


(rf/reg-event-db
 ;;  "Set initial? to true or false"
 :user-initial-visit
 (fn [db [_ true-or-false]]
   (assoc-in db [:ui :index :initial?] true-or-false)))

(rf/reg-sub
 :index-initial?
 (fn [db]
   (get-in db [:ui :index :initial?])))