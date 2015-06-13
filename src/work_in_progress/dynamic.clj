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
    (q/background 0)))

; Called once before each draw
; Takes old state as parameter and returns a new one
(defn update [state]
  (let [m (q/millis)]
    (cond
      (< m 10000) (if (= (:phase state) 0) (u/update-zero state) (u/state-zero state))
      (< m 25000) (if (= (:phase state) 1) (u/update-one state) (u/state-one state))
      (< m 35000) (if (= (:phase state) 2) (u/update-two state) (u/state-two state))
      :else state)))
