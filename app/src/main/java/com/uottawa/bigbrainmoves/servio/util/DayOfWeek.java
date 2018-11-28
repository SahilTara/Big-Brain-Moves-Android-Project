package com.uottawa.bigbrainmoves.servio.util;

public enum DayOfWeek {
    MONDAY {
        @Override
        public String toString() {
            return "Monday";
        }
    },
    TUESDAY {
        @Override
        public String toString() {
            return "Tuesday";
        }
    },
    WEDNESDAY {
        @Override
        public String toString() {
            return "Wednesday";
        }
    },
    THURSDAY {
        @Override
        public String toString() {
            return "Thursday";
        }
    },
    FRIDAY {
        @Override
        public String toString() {
            return "Friday";
        }
    },
    SATURDAY {
        @Override
        public String toString() {
            return "Saturday";
        }
    },
    SUNDAY {
        @Override
        public String toString() {
            return "Sunday";
        }
    },
    ANY {
        @Override
        public String toString() {
            return "Any";
        }
    }

}
