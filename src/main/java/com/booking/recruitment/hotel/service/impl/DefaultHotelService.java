package com.booking.recruitment.hotel.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.booking.recruitment.hotel.exception.BadRequestException;
import com.booking.recruitment.hotel.exception.ElementNotFoundException;
import com.booking.recruitment.hotel.model.Hotel;
import com.booking.recruitment.hotel.repository.HotelRepository;
import com.booking.recruitment.hotel.service.HotelService;
import com.booking.recruitment.hotel.util.DistanceCalculator;

@Service
class DefaultHotelService implements HotelService {
	private final HotelRepository hotelRepository;
	
	@Autowired
	DefaultHotelService(HotelRepository hotelRepository) {
		this.hotelRepository = hotelRepository;
	}

	@Override
	public List<Hotel> getAllHotels() {
		return hotelRepository.findAll();
	}

	@Override
	public List<Hotel> getHotelsByCity(Long cityId) {
		return hotelRepository.findAll().stream()
				.filter((hotel) -> cityId.equals(hotel.getCity().getId()))
				.collect(Collectors.toList());
	}

	@Override
	public Hotel createNewHotel(Hotel hotel) {
		if (hotel.getId() != null) {
			throw new BadRequestException("The ID must not be provided when creating a new Hotel");
		}

		return hotelRepository.save(hotel);
	}

	@Override
	public Hotel getHotel(Long hotelId) {
		if(hotelId == null) {
			throw new BadRequestException("The ID is not provided to get the hotel details");
		}
		Optional<Hotel> hotel = hotelRepository.findById(hotelId);
		if(!hotel.isPresent()) {
			throw new ElementNotFoundException("The hotel does not exist for the given ID");
		}
		return hotel.get();
	}
	
	@Override
	public void deleteHotel(Long hotelId) {
		if(hotelId == null) {
			throw new BadRequestException("The ID is not provided to delete the hotel details");
		}
		Optional<Hotel> hotelOpt = hotelRepository.findById(hotelId);
		if(!hotelOpt.isPresent()) {
			throw new ElementNotFoundException("The hotel does not exist for the given ID");
		}
		Hotel hotel = hotelOpt.get();
		hotel.setDeleted(true);
		hotelRepository.save(hotel);
	}
	
	@Override
	public List<Hotel> getHotelsByCity(Long cityId, String sortedBy) {
		if(cityId == null) {
			throw new BadRequestException("The city ID is not provided to get the hotels");
		}
		// putting a hard check of sort by distance for now
		if(!sortedBy.equalsIgnoreCase("distance")) {
			throw new BadRequestException("The sort by is only supported for distance");
		}
		List<Hotel> hotels = getHotelsByCity(cityId);
		double cityCentreLatitude = hotels.get(0).getCity().getCityCentreLatitude();
		double cityCentreLongitude = hotels.get(0).getCity().getCityCentreLongitude();
		for(Hotel hotel : hotels) {
			double distance = DistanceCalculator.getDistance(cityCentreLongitude, cityCentreLatitude,
					hotel.getLongitude(), hotel.getLongitude());
			hotel.setDistanceFromCityCentre(distance);
		}
		return hotels.stream()
				.sorted(Comparator.comparingDouble(Hotel::getDistanceFromCityCentre))
				.limit(3)
				.collect(Collectors.toList());
	}
}
