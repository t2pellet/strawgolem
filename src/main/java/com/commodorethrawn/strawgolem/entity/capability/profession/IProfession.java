package com.commodorethrawn.strawgolem.entity.capability.profession;

import java.util.HashMap;
import java.util.Map;

public interface IProfession {

    enum GolemProfession {
        FARMER(0),
        RANCHER(1);

        private static final Map<Integer, GolemProfession> _professionMap = new HashMap<>();
        static {
            for (GolemProfession profession : GolemProfession.values()) _professionMap.put(profession.Value, profession);
        }

        public static GolemProfession valueOf(int i) {
            return _professionMap.get(i);
        }

        public final int Value;

        private GolemProfession(int value) {
            Value = value;
        }


    }

    GolemProfession getProfession();

    void setProfession(GolemProfession profession);



}
