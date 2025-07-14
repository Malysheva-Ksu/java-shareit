package ru.practicum.booking.dto;

public enum BookingStatus {
    WAITING,    // Ожидает подтверждения
    APPROVED,   // Подтверждено владельцем
    REJECTED,   // Отклонено владельцем
    CANCELED    // Отменено создателем бронирования
}