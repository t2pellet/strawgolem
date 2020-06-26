package com.commodorethrawn.strawgolem.entity.capability.profession;

public class Profession implements IProfession {

    private GolemProfession profession;

    public Profession() {
        profession = GolemProfession.FARMER;
    }

    @Override
    public GolemProfession getProfession() {
        return profession;
    }

    @Override
    public void setProfession(GolemProfession profession) {
        this.profession = profession;
    }
}
