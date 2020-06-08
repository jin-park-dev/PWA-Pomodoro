(ns state.db)


(def default-db {:name "PWAdoro"})

; TODO: Pull this out to env file
(def default-development-settings {:dev {:dev? true
                                         :dev-panel? true}})

(def default-user-settings {:dev {:dev? false}
                            :ui {:clock {:nav "simple"}
                                 :timer {:nav "simple"}
                                 :pomodoro {:nav "simple"}  ; simple(clean) simple-options(adv) advanced?
                                 }})
