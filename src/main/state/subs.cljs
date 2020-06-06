(ns state.subs
  (:require
   [re-frame.core :as rf]
   [state.db :as db]))


(rf/reg-sub
 :name
 (fn [db]
   (:name db)))

(rf/reg-sub
 :dev?
 (fn [db]
   (get-in db [:dev :dev?])))

(rf/reg-sub
 :dev-panel?
 (fn [db]
   (get-in db [:dev :dev?])))


(comment
  (pr-str @(rf/subscribe [:dev?])) 
  )