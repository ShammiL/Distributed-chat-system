package org.example.models.requests;

public class MoveJoinRequest extends AbstractRoomRequest {

    private String identity;
    private String former;

    public MoveJoinRequest(String former, String identity, String roomId) {
        super(RequestConstants.MOVE_JOIN, roomId);
        this.former = former;
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }

    public String getFormer() {
        return former;
    }
}
