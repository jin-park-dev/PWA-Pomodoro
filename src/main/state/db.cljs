(ns state.db)

(def theme ["digital-clean-azure" "digital-clean-jade"])


(def default-db {:name "PWAdoro"})

; TODO: Pull this out to env file
(def default-development-settings {:dev {:dev? true
                                         :dev-panel? true}})

(def default-user-settings {:dev {:dev? false}
                            :ui {:index {:initial? true  ; First time user is on site (for this session?)
                                         :last-seen nil
                                         }
                                 :clock {:nav "simple"}
                                 :timer {:nav "simple"}
                                 :pomodoro {:nav "simple"}  ; simple(clean) simple-options(adv) advanced?
                                 }
                            ; Overall theme of the page. Temping to do customization here but it will only work only for simple-clean. Once vue-style it hard to compose without
                            ; ending up in a mess with one need some, other not.
                            ; So decision to put as much as I can in tailwind and small customization there.
                            :theme {:time {:style "digital-clean-jade"}  ; This can be digital or something else?
                                    }  
                            })
