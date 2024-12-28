package kr.rtuserver.supplybox.loot;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class Loot {

    private final String name;

    private int selectMin = 2;
    private int selectMax = 2;

    private List<Item> list = new ArrayList<>();

    private int chance = 0;

    public void addItem(Item item) {
        chance += item.getChance();
        list.add(item);
    }

    @Data
    @RequiredArgsConstructor
    public static class Item {

        private final String item;

        private int min = 1;
        private int max = 64;

        private int chance = 100;

    }
}
