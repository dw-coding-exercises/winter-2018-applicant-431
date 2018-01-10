(ns my-exercise.search-test
  (:require [clojure.test :refer :all]
            [my-exercise.search :refer :all]))

;;don't really have time for tests, but wanted to have at least one

(deftest state-ocd-id-test
  (testing "OR state ocd-id generated correctly"
           (is (= "ocd-division/country:us/state:or"
                  (state-ocd-id "OR")))))


