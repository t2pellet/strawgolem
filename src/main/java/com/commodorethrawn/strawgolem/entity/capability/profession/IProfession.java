package com.commodorethrawn.strawgolem.entity.capability.profession;

import java.util.HashMap;
import java.util.Map;

public interface IProfession {

    /**
     * @return the golems profession
     */
    GolemProfession getProfession();

    /**
     * Sets the golem's profession to 'profession'
     *
     * @param profession the desired profession
     */
    void setProfession(GolemProfession profession);

    enum GolemProfession {
        FARMER(0),
        RANCHER(1);

        /* Needed for GolemProfession::valueOf to work */
        private static final Map<Integer, GolemProfession> _professionMap = new HashMap<>();

        static {
            for (GolemProfession profession : GolemProfession.values())
                _professionMap.put(profession.Value, profession);
        }

        public final int Value;

        GolemProfession(int value) {
            Value = value;
        }

        /**
         * Get the profession corresponding to that integer
         *
         * @param i the integer value of the profession
         * @return the profession
         */
        public static GolemProfession valueOf(int i) {
            return _professionMap.get(i);
        }

    }


}
