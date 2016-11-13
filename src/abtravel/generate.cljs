(ns abtravel.generate)

(defrecord Station [name lat long])
(defrecord Line [id stations])
(defrecord Bus [id number lat long])

(def st [{:id 1 :name "Gara"}
		 {:id 2 :name "Avram Iancu"}
		 {:id 3 :name "P.ta Alessandria"}
		 {:id 4 :name "Unirea"}
		 {:id 5 :name "Alba Mall"}
		 {:id 6 :name "Ampoi 3"}
		 {:id 7 :name "Ardealu"}
		 {:id 8 :name "Profi"}
		 {:id 9 :name "Mercur"}
		 {:id 10 :name "Piata Cetate"}
		 {:id 11 :name "Spital"}
		 {:id 12 :name "Sf. Ecaterina"}
		 {:id 13 :name "Revolutiei 1989"}
		 {:id 14 :name "Orhideelor"}
		 {:id 15 :name "Camin Varstnici"}
		 {:id 16 :name "Schit"} ;;103A
		 {:id 17 :name "Incoronarii"}
		 {:id 18 :name "Cimitirul Eroilor"} ;;103
		 {:id 19 :name "Ambient"}
		 {:id 20 :name "Aprohorea"}
		 {:id 21 :name "Macului"}
		 {:id 22 :name "Ampoi 2"} ;;105C
		 {:id 23 :name "Kaufland"}
		 {:id 24 :name "Abrudului"}
		 {:id 25 :name "Micesti 1"}
		 {:id 26 :name "Micesti 2"}
		 {:id 27 :name "Micesti 3"}
		 {:id 28 :name "Micesti"} ;;110
		 {:id 29 :name "Centru de zi"}
		 {:id 30 :name "Casa Armatei"}
		 {:id 31 :name "Regina Maria"}
		 {:id 32 :name "Motilor 1"} ;;101B
		 {:id 33 :name "Vila Elisabeta"}
		 {:id 34 :name "Albac"}
		 {:id 35 :name "Grigorescu"}
		 ])

(def lines [{:id "101B" :stations [29 31 30 32 9 10 11 12 13 21 17 18 1]}
		   {:id "110" :stations [1 2 3 4 5 6 7 8 23 33 34 24 25 26 27 28]}
		   {:id "105C" :stations [19 20 22 6 7 8 9 10 11 12 13 21 17 18 1]}
		   {:id "103" :stations [1 2 3 4 5 6 7 8 9 10 11 12 13 21 17 18 1]}
	   	   {:id "103A" :stations [1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 35 16]}])

(def buses [{:id 1 :number "CJ 13 GEW" :lat 0 :long 0}
		   {:id 2 :number "CJ 13 GNV" :lat 0 :long 0}
		   {:id 3 :number "AB 07 TTE" :lat 0 :long 0}
		   {:id 4 :number "AB 07 HEV" :lat 0 :long 0}
		   {:id 5 :number "B 25 ZRX" :lat 0 :long 0}
		   {:id 6 :number "B 11 XDV" :lat 0 :long 0}])
