(ns ponjure.core
  (:use [ponjure.game])
  (:import [org.newdawn.slick
            BasicGame
            ScalableGame
            AppGameContainer]))

(defmacro defproxy [name class-and-interfaces args & fs]
  `(def ~name (proxy ~class-and-interfaces ~args ~@fs)))

(defproxy ponjure [BasicGame] ["Ponjure"]
  (init [container] (init container))
  (update [container delta] (update container delta))
  (render [container graphics] (render container graphics)))

;(def game (AppGameContainer. ponjure))
(def game (AppGameContainer. (ScalableGame. ponjure 800 600)))

(doto game
  (.setDisplayMode 800 600 false)
  (.setShowFPS false)
  (.start))
