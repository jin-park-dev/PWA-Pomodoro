(ns state.events
  (:require
   [re-frame.core :as rf]
   [state.db :as db]))

; v3 Above version, When app re-loads it will overwrite whole database.
; Eric is opinionated that improvement can be made by only setting default key rather then replacing whole db with default.
; This will keep caches, and other parts still in DB with re-loading.
; I think there could be issue if there are calculated values that gets stored in re-frame and app will need to be very very carefully designed to avoid these pitfall. Then it can work. Probably easier to just replace to starting state for many people.
; Eric say this really depends on domain, and have to make choice.
(rf/reg-event-fx
 ::initialize-db
 (fn [{:keys [db]} _] ; 1. Take out keys only
   {:db (merge db db/default-db) ; 2. Replace default original DB only.
    ; :ajax {} ;  Can add more initial effects here like ajax calls.
    }
   ))

(rf/reg-event-db
 ::initialize-db-dev
 (fn [db]
   (assoc-in db [:dev :dev?] false)
   (assoc-in db [:dev :dev-panel?] false))  ;; TODO: Pull this out to config file.
 )

#_(rf/reg-event-db
 ::set-active-panel
 (fn [db [_ active-panel]]
   (assoc db :active-panel active-panel)))

(rf/reg-event-db
 :dev/dev-switch
 (fn [db [_]]
;;    (println "rf/reg-event-db :dev/dev-switch")
;;    (println "db: "db)
;;    (println "_ - " _)
   (update-in db [:dev :dev?] not)))

(rf/reg-event-db
 :dev/dev-panel-switch
 (fn [db [_]]
   (update-in db [:dev :dev-panel?] not)))

(comment
  (pr-str @(rf/subscribe [:dev?]))
  (rf/dispatch [:dev/dev-switch])

  (pr-str @(rf/subscribe [:dev-panel?]))
  (rf/dispatch [:dev/dev-panel-switch])
  )