package com.commodorethrawn.strawgolem.entity.capability.profession;

import java.util.HashMap;
import java.util.Map;

public interface IProfession {

    enum GolemProfession {
        FARMER(0),
        RANCHER(1);

        /* Needed for GolemProfession::valueOf to work */
        private static final Map<Integer, GolemProfession> _professionMap = new HashMap<>();
        static {
            for (GolemProfession profession : GolemProfession.values()) _professionMap.put(profession.Value, profession);
        }

        /**
         * Get the profession corresponding to that integer
         * @param i
         * @return the profession
         */
        public static GolemProfession valueOf(int i) {
            return _professionMap.get(i);
        }

        public final int Value;

        private GolemProfession(int value) {
            Value = value;
        }

    }

    /**
     * @return the golems profession
     */
    GolemProfession getProfession();

    /**
     * Sets the golem's profession to 'profession'
     * @param profession
     */
    void setProfession(GolemProfession profession);



}
