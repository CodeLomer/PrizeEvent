package com.github.codelomer.prizeEvent.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.List;
@RequiredArgsConstructor
@Getter
public class DecorateButton {
    private final ItemStack itemStack;
    private final List<Integer> slots;

}
