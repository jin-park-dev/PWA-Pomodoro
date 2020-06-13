(ns state.events
  (:require
   [re-frame.core :as rf]
   [state.db :refer [default-db default-development-settings default-user-settings]]))

; v3 Above version, When app re-loads it will overwrite whole database.
; Eric is opinionated that improvement can be made by only setting default key rather then replacing whole db with default.
; This will keep caches, and other parts still in DB with re-loading.
; I think there could be issue if there are calculated values that gets stored in re-frame and app will need to be very very carefully designed to avoid these pitfall. Then it can work. Probably easier to just replace to starting state for many people.
; Eric say this really depends on domain, and have to make choice.
(rf/reg-event-fx
 ::initialize-db
 (fn [{:keys [db]} _] ; 1. Take out keys only
   {:db (merge db default-db) ; 2. Replace default original DB only.
    ; :ajax {} ;  Can add more initial effects here like ajax calls.
    }
   ))


(comment
  (println default-db)
  (pr-str default-db)
  (pr-str default-development-settings)
  (pr-str default-user-settings)
  (pr-str (type default-db))
  
  (merge default-db default-user-settings)
  )


(rf/reg-event-db
 ::initialize-db-dev
 (fn [db]
   (merge db default-development-settings)))

(rf/reg-event-db
 ::initialize-db-user-settings
 (fn [db]
   (merge db default-user-settings)))

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


; Theme changing
(rf/reg-event-db
 :theme/set-text-color
 (fn [db [_ color]]
   (assoc-in db [:theme :general] color)))


(comment
  (pr-str @(rf/subscribe [:dev?]))
  (rf/dispatch [:dev/dev-switch])

  (pr-str @(rf/subscribe [:dev-panel?]))
  (rf/dispatch [:dev/dev-panel-switch])
  )