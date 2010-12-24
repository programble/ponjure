(ns ponjure.game
  (:import [org.newdawn.slick
            Color
            Input]
            [org.newdawn.slick.geom Rectangle]))

(def player (atom {:x 15 :y 300 :height 75 :width 10 :score 0}))
(def computer (atom {:x 775 :y 300 :height 75 :width 10 :score 0}))
(def ball (atom {:x 395 :y 295 :height 10 :width 10 :speed 0.0 :angle 45.0}))
(def debug (atom false))

(defn init [container])

(defn update-input [container delta input]
  (when (.isKeyPressed input Input/KEY_ESCAPE)
    (.exit container))
  
  (when (.isKeyPressed input Input/KEY_F11)
    (.setFullscreen container (not (.isFullscreen container))))
  
  (when (.isKeyPressed input Input/KEY_F3)
    (.setShowFPS container (not (.isShowingFPS container)))
    (swap! debug not))
  
  (when (.isKeyDown input Input/KEY_W)
    (if (>= (:y @player) 0)
      (swap! player assoc :y (- (:y @player) (* 0.4 delta)))))
  
  (when (.isKeyDown input Input/KEY_S)
    (if (<= (+ (:y @player) (:height @player)) 600)
      (swap! player assoc :y (+ (:y @player) (* 0.4 delta)))))

  (when (and (.isKeyDown input Input/KEY_SPACE) (= (:speed @ball) 0.0))
    (swap! ball assoc :speed 0.2)))

(defn update-ball [container delta]
  ; Top/Bottom Collisions
  (when (<= (:y @ball) 0)
    (swap! ball assoc :angle (- 180 (:angle @ball) (/ 2 180)))
    (swap! ball assoc :y 0))
  (when (>= (+ (:y @ball) (:height @ball)) 600)
    (swap! ball assoc :angle (- 180 (:angle @ball) (/ 2 180)))
    (swap! ball assoc :y (- 600 (:height @ball))))

  ; Scoring collisions
  (when (<= (:x @ball) 0)
    (swap! computer assoc :score (inc (:score @computer)))
    (swap! ball assoc :x 395 :y 295 :speed 0.0))
  (when (>= (+ (:x @ball) (:width @ball)) 800)
    (swap! player assoc :score (inc (:score @player)))
    (swap! ball assoc :x 395 :y 295 :speed 0.0))

  ; Paddle collisions
  (let [ballrect (Rectangle. (:x @ball) (:y @ball) (:width @ball) (:height @ball))
        playerrect (Rectangle. (:x @player) (:y @player) (:width @player) (:height @player))
        computerrect (Rectangle. (:x @computer) (:y @computer) (:width @computer) (:height @computer))]
    (when (.intersects ballrect playerrect)
      (swap! ball assoc :angle (- (:angle @ball)))
      (swap! ball assoc :speed (+ (:speed @ball) 0.02))
      (swap! ball assoc :x (+ (:x @player) (:width @player))))
    (when (.intersects ballrect computerrect)
      (swap! ball assoc :angle (- (:angle @ball)))
      (swap! ball assoc :speed (+ (:speed @ball) 0.02))
      (swap! ball assoc :x (- (:x @computer) (:width @ball)))))
  
  ; Movement
  (let [hip (* (:speed @ball) delta)
        mx (* hip (Math/sin (Math/toRadians (:angle @ball))))
        my (* hip (Math/cos (Math/toRadians (:angle @ball))))]
    (swap! ball assoc :x (+ (:x @ball) mx))
    (swap! ball assoc :y (- (:y @ball) my))))

(defn update-computer [container delta]
  (swap! computer assoc :y (- (:y @ball) 20)))

(defn update [container delta]
  (update-input container delta (.getInput container))
  (update-ball container delta)
  (update-computer container delta))

(defn render [containter graphics]
  (.setBackground graphics Color/black)
  (.setColor graphics Color/white)
  (.fillRect graphics (:x @player) (:y @player) (:width @player) (:height @player))
  (.fillRect graphics (:x @computer) (:y @computer) (:width @computer) (:height @computer))
  (.fillRect graphics (:x @ball) (:y @ball) (:width @ball) (:height @ball))
  (.drawString graphics (str "Score: " (:score @player) " - " (:score @computer)) 10 10))
