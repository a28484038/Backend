package com.aieverywhere.backend.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aieverywhere.backend.dto.FriendsDTO;
import com.aieverywhere.backend.models.Relationship;
import com.aieverywhere.backend.models.Relationship.RelationshipStatus;
import com.aieverywhere.backend.models.Users;
import com.aieverywhere.backend.models.Users.Role;
import com.aieverywhere.backend.repostories.RelaRepo;

@Service
public class RelationshipServices {
	private final RelaRepo relaRepo;

	@Autowired
	private UsersServices usersServices;

	@Autowired
	public RelationshipServices(RelaRepo relaRepo) {
		this.relaRepo = relaRepo;
	}

	// create follow relationship
	public String createFollowRelationship(Relationship relationship) {
		relaRepo.save(relationship);
		return "create success";
	}

	// get all user friends relationship
	public List<Relationship> getAllUserFriends(Long UserId) {
		List<Relationship> allFriends = relaRepo.findAllByUserId(UserId);

		return allFriends;
	}

	// get all user friends relationship and can choose with role
	public List<Relationship> getAllUserFriendsWithRole(Long UserId, Role role) {
		List<Relationship> allFriendsWithRole = relaRepo.findAllByUserIdAndRole(UserId, role);

		return allFriendsWithRole;
	}

	@Transactional
	public void deleteRelationship(Long userId, Long friendId) {
        Long relationship = relaRepo.findRelationshipIdByUserIdAndFriendId(userId, friendId);
        if (relationship != null) {
            relaRepo.deleteByRelationshipId(relationship);
        } else {
            throw new RuntimeException("關係不存在");
        }
    }
	
	
	public Boolean checkFollowRelationship(Long userId, Long friendId){
		if (relaRepo.findRelationshipIdByUserIdAndFriendId(userId, friendId) != null) {
			System.out.println("Following");
			return true;
		} else {
			return false;
		}
	}

	public List<FriendsDTO> getFriendesList(Long userId) {

		// get all user friends relationship
		List<Relationship> relationships = relaRepo.findAllByUserIdAndRelationshipStatus(userId,
				RelationshipStatus.Friend);

		// get all user friends information
		return relationships.stream()
				.map(relationship -> {
					try {
						Users friend = usersServices.getUsersByUsersId(relationship.getFriendId());
						return new FriendsDTO(
								friend.getUserId(),
								friend.getNickName(),
								friend.getImagePath(),
								relationship.getFriendId());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return null;
					}

				})
				.collect(Collectors.toList());
	}
}
