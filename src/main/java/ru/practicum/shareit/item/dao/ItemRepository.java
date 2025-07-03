package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> getAllByOwnerId(Long ownerId);

    List<Item> findByAvailableIsTrueAndNameContainingIgnoreCaseOrAvailableIsTrueAndDescriptionContainingIgnoreCase(String nameText, String descriptionText);
}
