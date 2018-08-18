package com.amazingteam.competenceproject.util;


import com.amazingteam.competenceproject.model.Cloth;
import com.amazingteam.competenceproject.model.Tag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Algorithm {
    public static List<Cloth> getClothesByType(String t, List<Cloth> wardrobe) {
        List<Cloth> pickedCloths = new ArrayList<>();
        for (Cloth cloth : wardrobe) {
            if (t.equals(cloth.getType())) {
                pickedCloths.add(cloth);
            }
        }

        return pickedCloths;
    }

    static List<Cloth> getClothesByTag(String tag, List<Cloth> wardrobe) {
        List<Cloth> pickedCloths = new ArrayList<>();
        for (Cloth cloth : wardrobe) {
            for (Tag t : cloth.getTagList()) {
                if (t.getName().equals(tag)) {
                    pickedCloths.add(cloth);
                }
            }
        }

        return pickedCloths;
    }

    public static Cloth getWear(List<Cloth> wardrobe, String type, List<Tag> tags) {
        Random random = new Random();
        List<Cloth> wearList = getClothesByType(type, wardrobe);
        List<Cloth> wearSieved = new ArrayList<>();
        Collections.sort(tags);
        for (Tag t : tags) {
            System.out.println(t.getName());
        }
        for (Tag t : tags) {
            wearSieved.clear();
            wearSieved.addAll(getClothesByTag(t.getName(), wearList));
            if (wearSieved.size() != 0) {
                wearList.clear();
                wearList.addAll(wearSieved);
            }
        }
        return wearList.get(random.nextInt(wearList.size()));
    }

    public static int getClothesAmount(List<Cloth> clothList, String t)
    {
        return getClothesByType(t,clothList).size();
    }

}
