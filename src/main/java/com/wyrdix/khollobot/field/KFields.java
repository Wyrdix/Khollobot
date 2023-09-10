package com.wyrdix.khollobot.field;

import com.wyrdix.khollobot.KUser;

public class KFields {
    private static final KField<Long> DISCORD_ID = new KField<>() {
        @Override
        public Long get(KUser user) {
            return user.getDiscordId();
        }

        @Override
        public void set(KUser user, Long value) {
            throw new IllegalArgumentException("Discord Id can't be set");
        }
    };
}
