package messages;

import ros.msgs.std_msgs.Header;

public class ContactsState {
    public Header header = new Header();
    public ContactState[] states = new ContactState[1];

    
   public ContactsState(Header header, ContactState[] states){
        this.header = header;
        this.states = states;

    }
    public ContactsState(){}
}
