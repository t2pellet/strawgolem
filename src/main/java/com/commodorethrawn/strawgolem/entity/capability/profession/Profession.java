package com.commodorethrawn.strawgolem.entity.capability.profession;

public class Profession implements IProfession {

    private GolemProfession golemProfession;

    public Profession() {
        golemProfession = GolemProfession.FARMER;
    }

    @Override
    public GolemProfession get() {
        return golemProfession;
    }

    @Override
    public void set(GolemProfession profession) {
        golemProfession = profession;
    }
}
