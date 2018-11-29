package com.uottawa.bigbrainmoves.servio.util.enums;

public enum AccountType {
    SERVICE_PROVIDER {
        @Override
        public String toString() {
            return "Service Provider";
        }
    },
    HOME_OWNER {
        @Override
        public String toString() {
            return "Home Owner";
        }
    },
    ADMIN {
        @Override
        public String toString() {
            return "Admin";
        }
    },
    NONE {
        @Override
        public String toString() {
            return "NONE";
        }
    }
}
