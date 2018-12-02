package com.uottawa.bigbrainmoves.servio.util.enums;

public enum BookingStatus {
    PENDING {
        @Override
        public boolean isParsable() {
            return true;
        }

        @Override
        public String toString() {
            return "Pending";
        }
    },
    ACCEPTED {
        @Override
        public boolean isParsable() {
            return true;
        }

        @Override
        public String toString() {
            return "Accepted";
        }
    },
    COMPLETED {
        @Override
        public boolean isParsable() {
            return false;
        }

        @Override
        public String toString() {
            return "Completed";
        }
    },
    CANCELLED {
        @Override
        public boolean isParsable() {
            return false;
        }

        @Override
        public String toString() {
            return "Cancelled";
        }
    };

    public abstract boolean isParsable();
}
