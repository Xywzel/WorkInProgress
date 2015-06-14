(ns work-in-progress.dynamic
  (:require [quil.core :as q]
            [work-in-progress.drawers :as d]
            [work-in-progress.updates :as u]))

; This is run once at the start of the demo
; Returns initial state
(defn setup []
  (q/smooth)
  (q/frame-rate 60)
  (q/background 200)
  {:phase -1}
  )

; Draws the screen using state
(defn draw [state]
  (case (:phase state)
    0 (d/draw-phase-zero state)
    1 (d/draw-phase-one state)
    2 (d/draw-phase-two state)
    3 (d/draw-phase-three state)
    4 (d/draw-phase-four state)
    5 (q/background 0 0 0 15)
    (q/background 0 0 0 15)))

; Called once before each draw
; Takes old state as parameter and returns a new one
(defn update [state]
  (let [m (q/millis)]
    (cond
      (< m 5000)   (if (= (:phase state) 0) (u/update-zero state)  (u/state-zero state))
      (< m 15000)  (if (= (:phase state) 1) (u/update-one state)   (u/state-one state))
      (< m 20000)  (if (= (:phase state) 2) (u/update-two state)   (u/state-two state))
      (< m 32000)  (if (= (:phase state) 3) (u/update-three state) (u/state-three state))
      (< m 60000)  (if (= (:phase state) 4) (u/update-four state)  (u/state-four state))
      (< m 65000) (u/phase-five state)
      (> m 65500) (q/exit)
      :else state)))
