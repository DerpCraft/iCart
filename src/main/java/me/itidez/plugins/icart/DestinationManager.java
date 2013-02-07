package me.itidez.plugins.icart;

/* 
 *Author: iTidez
 */

public class DestinationManager {
	public Icart plugin;
	public Db db;
	
	public DestinationManager(Icart plugin, Db db) {
		this.plugin = plugin;	
		this.db = db;
	}
	
	public Destination getDestiantionFromName(String name) {
		ResultSet result = db.query("SELECT `id`, `location_x`, `location_y`, `location_z` FROM `signs` WHERE `id` = '"+name+"'");
		Location loc = new Location("world", result.getInt(2), result.getInt(3), result.getInt(4));
		Destination des = new Destination(name, loc);
		return des;
	}
	
	public Vector getDestinationVector(Destination des) {
		Vector vector = new Vector(des.getX(), des.getY(), des.getZ());
		return vector;
	}
	
	public Destination createDestination(String id, String type, String target, Location loc) {
		db.query("INSERT INTO `signs`(`id`,`target`,`location_x`,`location_y`,`location_z`,`type`) VALUES('"+id+"', '"+target+"', "+loc.getBlockX()+", "+loc.getBlockY()+", "+loc.getBlockZ()+", '"+type+"')");
		return new Destination(id, loc);
	}
	
	public Station createStation(String id, String type, Location loc, String[] actions) {
			db.query("CREATE TABLE IF NOT EXISTS `stations` (`id` VARCHAR(16) NOT NULL, `location` VARCHAR(100) NOT NULL, `actions` VARCHAR(1000) NOT NULL, PRIMARY KEY (`id`) ) ENGINE=MyISAM DEFAULT CHARSET=latin1;");
			if(id.isEmpty()) {
				return null;
			}
			if(!type.equalsIgnoreCase("station")) {
				return null;
			}
			if(loc == null) {
				return null;	
			}
			if(actions.length == 0) {
				actions[0] = "default";	
			}
			String location = loc.getBlockX()+","+loc.getBlockY()+","+loc.getBlockZ();
			String action;
			for(String a : actions) {
				action = action + a + ",";	
			}
			db.query("INSERT INTO `stations`(`id`,`location`,`actions`) VALUES('"+id+"', '"+location+"', "+action+")");
			return new Station(id, loc);
	}
}
//TODO: Include Skype messaging to staff chat from IRC integration API included below
/*
Creating chats and sending messages

This section contains the commands for creating chats and sending messages.

CHAT CREATE

This command creates a chat object.

Syntax
-> CHAT CREATE [<target>, <target>*]

Response
<- CHAT <chat_id> STATUS <value>

Version
Protocol 5, updated in protocol 7 (API version 3.0)

Parameters

    <target> – username(s) with whom to create a chat
    <chat_id> – chat identifier; string (usually looks like “#me/$target;012345679012345”)
    <value> – depends on the type of chat created: DIALOG for a 1:1 chat; MULTI_SUBSCRIBED for a chat with multiple participants

Notes

    From version 3.6 and later, opening chat windows (both from API and manually via UI) generate additional chat window open and close notfication messages. Refer to the Chat notifications section for more information.
    The CHAT CREATE command does not open a chat window; use the OPEN CHAT command to do so.
    Starting from protocol 7, the parameter(s) are no longer mandatory. If no usernames are passed in parameters, an empty multichat is created.

Example:

//------------------------------------------------------------------
// Creating chat with one target
-> CHAT CREATE anappo5
<- CHAT #anappo/$anappo5;2e4e763a2fc121ed STATUS DIALOG
-> OPEN CHAT #anappo/$anappo5;2e4e763a2fc121ed
<- OPEN CHAT #anappo/$anappo5;2e4e763a2fc121ed
//------------------------------------------------------------------
// Creating chat with no target
-> CHAT CREATE
<- CHAT #anappo/$72cb4c9d0871e6dc NAME #anappo/$72cb4c9d0871e6dc
<- CHAT #anappo/$72cb4c9d0871e6dc ACTIVITY_TIMESTAMP 0
<- CHAT #anappo/$72cb4c9d0871e6dc STATUS MULTI_SUBSCRIBED
<- CHAT #anappo/$72cb4c9d0871e6dc TYPE MULTICHAT
<- CHAT #anappo/$72cb4c9d0871e6dc STATUS UNSUBSCRIBED
<- CHATMEMBER 570 ROLE USER
<- CHAT #anappo/$72cb4c9d0871e6dc MYROLE USER
<- CHAT #anappo/$72cb4c9d0871e6dc MEMBERS anappo
<- CHAT #anappo/$72cb4c9d0871e6dc ACTIVEMEMBERS anappo
<- CHAT #anappo/$72cb4c9d0871e6dc MYSTATUS SUBSCRIBED
<- CHAT #anappo/$72cb4c9d0871e6dc STATUS MULTI_SUBSCRIBED
<- CHAT #anappo/$72cb4c9d0871e6dc TIMESTAMP 1175089677
-> OPEN CHAT #anappo/$72cb4c9d0871e6dc
<- OPEN CHAT #anappo/$72cb4c9d0871e6dc
//------------------------------------------------------------------
// Creating chat with two targets
-> CHAT CREATE anappo3, anappo5
<- CHAT #anappo/$8c9e3bb94643d668 NAME #anappo/$8c9e3bb94643d668
<- CHAT #anappo/$8c9e3bb94643d668 ACTIVITY_TIMESTAMP 0
<- CHAT #anappo/$8c9e3bb94643d668 STATUS MULTI_SUBSCRIBED
<- CHAT #anappo/$8c9e3bb94643d668 TYPE MULTICHAT
<- CHAT #anappo/$8c9e3bb94643d668 STATUS UNSUBSCRIBED
<- CHATMEMBER 585 ROLE USER
<- CHAT #anappo/$8c9e3bb94643d668 MYROLE USER
<- CHAT #anappo/$8c9e3bb94643d668 MEMBERS anappo
<- CHAT #anappo/$8c9e3bb94643d668 ACTIVEMEMBERS anappo
<- CHAT #anappo/$8c9e3bb94643d668 MYSTATUS SUBSCRIBED
<- CHAT #anappo/$8c9e3bb94643d668 STATUS MULTI_SUBSCRIBED
<- CHAT #anappo/$8c9e3bb94643d668 TIMESTAMP 1175089858
<- CHAT #anappo/$8c9e3bb94643d668 MEMBERS anappo anappo3 anappo5
<- CHAT #anappo/$8c9e3bb94643d668 FRIENDLYNAME anappo3, anappo5
-> OPEN CHAT #anappo/$8c9e3bb94643d668
<- OPEN CHAT #anappo/$8c9e3bb94643d668

Error codes:

615, “CHAT: chat with given contact is disabled” – added in Skype version 3.5 (protocol 8)

CHATMESSAGE

Syntax
CHATMESSAGE <chat_id> <message>

Response
CHATMESSAGE <id> STATUS SENDING

Parameters

    <chat_id> – chat identifier
    <message> – message text body to send
    <id> – chatmessage identifier

Version
Protocol 5

Errors

    ERROR 510 Invalid/unknown chat name given
    Chat with does not exist
    ERROR 511 Sending a message to chat fails
    Could not send message to chat (eg. not a member)

ALTER CHAT SETTOPIC

Changes chat topic.

Syntax:
-> ALTER CHAT <chat_id> SETTOPIC <topic>

<- ALTER CHAT SETTOPIC

See also ALTER CHAT SETTOPICXML command.

Version
Protocol 5

Errors

    ERROR 501 CHAT: No chat found for given chat
    Chat with does not exist

ALTER CHAT SETTOPICXML

Enables you to set a chat topic that contains XML formatting elements. Note that the standard chat topic will be updated as well, stripped of XML tags.

Syntax:
-> ALTER CHAT <chat_id> SETTOPICXML <topic>

<- ALTER CHAT SETTOPICXML

Example (without feedback notifications):

-> ALTER CHAT #test/$b9275b3b334341f2 SETTOPICXML <BLINK>topic is blinking</BLINK>
-> ALTER CHAT #test/$b9275b3b334341f2 SETTOPICXML <B>topic in bold</B>
-> ALTER CHAT #test/$b9275b3b334341f2 SETTOPICXML <I>topic in italic</I>
-> ALTER CHAT #test/$b9275b3b334341f2 SETTOPICXML <U>topic with underline</U>
-> ALTER CHAT #test/$b9275b3b334341f2 SETTOPICXML Smiley: <SS type="smile">:-)</SS>
-> ALTER CHAT #test/$b9275b3b334341f2 SETTOPICXML <FONT COLOR="#FF0010">topic in red</FONT>

Version
Protocol 7 (API version 3.0)

ALTER CHAT ADDMEMBERS

This command adds new members to a chat.

Syntax:
-> ALTER CHAT <chat_id> ADDMEMBERS <target>[, <target>]*

<- ALTER CHAT ADDMEMBERS

Version
Protocol 5

Errors

    ERROR 501 CHAT: No chat found for given chat
    Chat with does not exist
    ERROR 504 CHAT: Action failed
    Could not add members into chat (eg is already a member; you have left chat)

ALTER CHAT LEAVE

This command causes user to leave the chat.

Syntax:
-> ALTER CHAT <chat_id> LEAVE

<- ALTER CHAT LEAVE

Errors

    ERROR 501 CHAT: No chat found for given chat
    Chat with does not exist
    ERROR 504 CHAT: Action failed
    Could not leave chat (for example if the user has already left this chat)

ALTER CHAT BOOKMARKED

Adds chat to the list of bookmarked chats.

Syntax to bookmark a chat:
-> ALTER CHAT <chat_id> BOOKMARK

<- ALTER CHAT <ID> BOOKMARKED TRUE

Syntax to remove a chat from list of bookmarked chats:
-> ALTER CHAT <ID> UNBOOKMARK

<- ALTER CHAT <ID> BOOKMARKED FALSE

Refer to following SEARCH commands on how to obtain a chat ID

SEARCH CHATS

SEARCH ACTIVECHATS

SEARCH MISSEDCHATS

SEARCH RECENTCHATS

SEARCH BOOKMARKEDCHATS

Version
Protocol 6, Skype API version 2.5

GET CHAT CHATMESSAGES

Returns IDs of chatmessage objects in a specified chat.

Syntax:
-> GET CHAT <chat_id> CHATMESSAGES

<- CHAT <chat_id> CHATMESSAGES <id>[, <id>]*

Version:
Protocol 5

Errors

    ERROR 501 CHAT: No chat found for given chat
    Chat with does not exist

GET CHAT RECENTCHATMESSAGES

Syntax
GET CHAT <chat_id> RECENTCHATMESSAGES

Response
CHAT <chat_id> RECENTCHATMESSAGES <id>[, <id>]*

Version
Protocol 5

Errors

    ERROR 501 CHAT: No chat found for given chat
    Chat with does not exist

SET CHATMESSAGE SEEN

Syntax
SET CHATMESSAGE <id> SEEN

Response
CHATMESSAGE <id> STATUS <value>

Parameters

    <id> – chat message ID.
    <value> – new value for chat message status; refer to CHATMESSAGE object for status values

Version
Protocol 3

Example

-> SET CHATMESSAGE 61 SEEN
<- CHATMESSAGE 61 STATUS READ

Errors

ERROR 18 SET: invalid WHAT
CHATMESSAGE command is missing or misspelled

ERROR 31 Unknown message id
Unknown chat message ID

ERROR 30 Invalid message id
Chat message ID is misspelled or contains non-permitted symbols (numeric are permitted)

ERROR 32 Invalid WHAT
Invalid status given to chat message, for example the message is already marked as seen

SET CHATMESSAGE BODY

This command enables you to change the text of a chat message.

Syntax:
-> SET CHATMESSAGE <chatmessage_id> BODY <text>

Weather a chat message text is changeable can be determined by checking the IS_EDITABLE property of a CHATMESSAGE object.

The rules for allowing editing are:

    Everyone can change their own messages.
    Creator of public chat can edit messages from others.
    Masters can edit messages originating from others, except those from the chat creator.
    Helpers and below cannot edit messages from others.

Refer to CHAT ROLES section for the list of chat roles.

Example:

//----------------------------------------------------------------
// First lets send out a chat message
-> CHATMESSAGE #anappo/$a1044019f5dc8c48 Test chat message
<- CHATMESSAGE 864 STATUS SENDING
<- CHAT #anappo/$a1044019f5dc8c48 ACTIVITY_TIMESTAMP 1175093328
<- CHATMESSAGE 864 STATUS SENT
//----------------------------------------------------------------
// Then lets see if we can edit it..
-> GET CHATMESSAGE 864 IS_EDITABLE
<- CHATMESSAGE 864 IS_EDITABLE TRUE
//----------------------------------------------------------------
// Then see if we can change the message text
-> SET CHATMESSAGE 864 BODY Test message after being edited
<- CHATMESSAGE 864 BODY Test message after being edited
<- CHATMESSAGE 864 EDITED_TIMESTAMP 1175093385
<- CHATMESSAGE 864 EDITED_BY anappo
<- CHATMESSAGE 864 BODY Test message after being edited

Version
Protocol 7 (API version 3.0)

SET MESSAGE SEEN – obsolete

Mark message as seen by the user and remove it from the missed messages list. This command is obsolete and has been replaced by the SET CHATMESSAGE SEEN command.

Syntax
SET MESSAGE <id> SEEN

Response
MESSAGE <id> STATUS value

Properties

    <id> – message ID;
    value – (new) status value

Version
Protocol 1, deprecated in protocol 3

Example

-> SET MESSAGE 1578 SEEN
<- MESSAGE 1578 STATUS READ

Errors

    ERROR 18 SET: invalid WHAT
    Object name missing or misspelled.
    ERROR 30 Invalid message id
    ID includes other than numeric characters.
    ERROR 31 Unknown message id
    Message with specified ID does not exist in current user’s message history.
    ERROR 32 Invalid WHAT
    Property name missing or misspelled.

MESSAGE – obsolete

The MESSAGE command is obsolete and has been replaced by the CHATMESSAGE command.

Syntax
MESSAGE <target> <text>

Response
CHATMESSAGE <id> STATUS SENDING (protocol 3 and up)
MESSAGE <id> STATUS SENDING (protocol 1 and 2)

Parameters

    <target> – target username to whom to send the message
    <text> – message body, for example Please call me

Version
Protocol 1

Errors

ERROR 26 Invalid user handle
The target username is missing or includes symbols which are not premitted

ERROR 43 Cannot send empty message
The message has no body.

Notes
When message sending fails, a LEFT-type message is received. The message’s LEAVEREASON shows why it failed. See the CHATMESSAGE object for a description.

Example

-> MESSAGE echo123 Please call me
<- MESSAGE 982 STATUS SENDING
<- MESSAGE 982 STATUS SENT

GET CHAT MEMBEROBJECTS

This command provides list of CHATMEMBER object IDs that represent chat participants.

Syntax:
GET CHAT <id> MEMBEROBJECTS

Refer to

    CHATMEMBER object for a list of CHATMEMBER properties.
    GET CHATMEMBER command for how to access CHATMEMBER properties.
    SEARCH CHATS for how to get a list of CHAT IDs.

Example:

-> GET CHAT #test/$test3;5f7cdbdd32dc731c MEMBEROBJECTS
<- CHAT #test/$3;5f7cdbdd32dc731c MEMBEROBJECTS 453, 454, 1465

Version
Protocol 7 (API version 3.0)

GET CHATMEMBER

This command provides read access to objects representing chat participants.

Syntax:
GET CHATMEMBER <id> <property>

Refer to

    CHATMEMBER object for a list of object properties.
    GET CHAT MEMBEROBJECTS command for a list of object IDs.

Example:

-> GET CHAT #test/$test3;5f7cdbdd32dc731c MEMBEROBJECTS
<- CHAT #test/$3;5f7cdbdd32dc731c MEMBEROBJECTS 453, 454, 1465
-> GET CHATMEMBER 1465 IDENTITY
<- CHATMEMBER 1465 IDENTITY test_p
-> GET CHATMEMBER 1465 CHATNAME
<- CHATMEMBER 1465 CHATNAME #test/$test3;5f7cdbdd32dc731c
-> GET CHATMEMBER 1465 ROLE
<- CHATMEMBER 1465 ROLE USER
-> GET CHATMEMBER 1465 IS_ACTIVE
<- CHATMEMBER 1465 IS_ACTIVE TRUE

Version
Protocol 7 (API version 3.0)

ALTER CHAT JOIN

This command enables you to re-join a Public chat that you have previously left. This command assumes a CHAT object is already present in the local system.

Note that this command does work with non-public multichats.
*

Syntax:
ALTER CHAT <chat_id> JOIN

Example:

//----------------------------------------------------------------------------
// Leaving public chat #anappo/$a1044019f5dc8c48
-> ALTER CHAT #anappo/$a1044019f5dc8c48 LEAVE
<- ALTER CHAT LEAVE
<- MESSAGE 392 STATUS SENDING
<- CHAT #anappo/$a1044019f5dc8c48 MEMBERS anappo2 anappo3 
<- CHAT #anappo/$a1044019f5dc8c48 ACTIVEMEMBERS anappo2 anappo3 
<- CHAT #anappo/$a1044019f5dc8c48 MYSTATUS UNSUBSCRIBED
<- CHAT #anappo/$a1044019f5dc8c48 STATUS UNSUBSCRIBED
<- CHAT #anappo/$a1044019f5dc8c48 BOOKMARKED FALSE
<- MESSAGE 392 STATUS SENT
//----------------------------------------------------------------------------
// Re-joining the chat
-> ALTER CHAT #anappo/$a1044019f5dc8c48 JOIN
<- CHAT #anappo/$a1044019f5dc8c48 MYSTATUS CONNECTING
<- CHAT #anappo/$a1044019f5dc8c48 STATUS UNSUBSCRIBED
<- ALTER CHAT JOIN
<- CHAT #anappo/$a1044019f5dc8c48 MEMBERS anappo2 anappo3
<- CHAT #anappo/$a1044019f5dc8c48 ACTIVEMEMBERS anappo2 anappo3
<- CHAT #anappo/$a1044019f5dc8c48 BOOKMARKED TRUE
<- CHAT #anappo/$a1044019f5dc8c48 MEMBERS anappo2 anappo3 
<- CHAT #anappo/$a1044019f5dc8c48 ACTIVEMEMBERS anappo2 anappo3 
<- CHAT #anappo/$a1044019f5dc8c48 MYSTATUS WAITING_REMOTE_ACCEPT
<- CHAT #anappo/$a1044019f5dc8c48 STATUS UNSUBSCRIBED
<- CHATMEMBER 75 IS_ACTIVE FALSE
<- CHATMEMBER 396 IS_ACTIVE FALSE
<- CHAT #anappo/$a1044019f5dc8c48 MEMBERS anappo anappo2 anappo3 
<- CHAT #anappo/$a1044019f5dc8c48 ACTIVEMEMBERS anappo anappo3
<- CHAT #anappo/$a1044019f5dc8c48 MYSTATUS SUBSCRIBED
<- CHAT #anappo/$a1044019f5dc8c48 STATUS MULTI_SUBSCRIBED
<- CHATMEMBER 75 IS_ACTIVE TRUE
<- CHATMEMBER 396 IS_ACTIVE TRUE
<- CHAT #anappo/$a1044019f5dc8c48 ACTIVEMEMBERS anappo anappo2 anappo3 
<- MESSAGE 398 STATUS READ

Errors

    ERROR 504 CHAT: action failed
    Attempt to re-join dialog or multichat. This command only enables you to re-join public chats.

Version
Protocol 7 (API version 3.0)

ALTER CHAT CLEARRECENTMESSAGES

This command clears recent chat messages in a given chat. Note that this command does not actually update user interface when a Skype client chat window for that chat is open. To see the effect, close the chat window and re-open it.

Syntax:
ALTER CHAT <chat_id> CLEARRECENTMESSAGES

Example:

-> ALTER CHAT #anappo/$test_p;297fcefb07ffc4b2 CLEARRECENTMESSAGES
<- ALTER CHAT CLEARRECENTMESSAGES

Version
Protocol 7 (API version 3.0)

ALTER CHAT SETALERTSTRING

This command enables you to set up a chat alert string. Normally, a small notification window will pop up at system tray when someone posts a message in a chat while the chat window is closed. When an alert string is set, the notification window will only appear when the message contains value set in SETALERTSTRING property.

Note that when setting this value from API, first symbol of the alert string is assumed to be “=” and gets stripped. To prevent first symbol of your alert string from being stripped, add “=” in front of it.

Syntax:
ALTER CHAT <chat_id> SETALERTSTRING <alert_string>

Example:

-> ALTER CHAT #anappo/$a1044019f5dc8c48 SETALERTSTRING "=test"
<- ALTER CHAT SETALERTSTRING

Version
Protocol 7 (API version 3.0)

ALTER CHAT ACCEPTADD

This command is used for accepting invitations to shared contact groups. In other chat contexts, invitations are either accepted or declined automatically, depending on user’s privacy settings.

Syntax:
ALTER CHAT <chat_id> ACCEPTADD

Version
Protocol 7 (API version 3.0)

ALTER CHAT DISBAND

This command removes all chat participants from the chat and closes it.

Syntax:
ALTER CHAT <chat_id> DISBAND

Example:

-> ALTER CHAT #anappo/$a1044019f5dc8c48 DISBAND
<- ALTER CHAT DISBAND
<- CHAT #anappo/$a1044019f5dc8c48 MYSTATUS CHAT_DISBANDED
<- CHAT #anappo/$a1044019f5dc8c48 STATUS UNSUBSCRIBED

Version
Protocol 7 (API version 3.0)

Public Chats

Public Chats were introduced in API version 3.0 Public Chats are an extension of existing multichat functionality.

From API point of view, public chats differ from multichats in that:

    CREATE command works somewhat differently, as a public chat identifier is formed differently from multichats;

Public chats have a user hierarchy with different privilege levels and a set of tools for chat administration (similar to administration of IRC channels). These administration tools are actually available for standard multichats as well (API commands such as KICK work in multichats, altho the Skype user interface for setting privileges is not available for multichats).

More or less everything listed under Creating chats and sending messages section is also applicable to public chats. The list of sections below is specific to public chats.

CHAT ROLES and PRIVILEGES

    CREATOR – member who created the chat. There can be only one creator per chat. Only creator can promote other members to masters.
    MASTER – Also known as chat hosts. Masters cannot promote other people to masters.
    HELPER – a semi-privileged member. Helpers will not be affected by the USERS_ARE_LISTENERS option. Helpers cannot promote or demote other members.
    USER – regular members who can post messages into the chat.
    LISTENER – a demoted member who can only receive messages but not post anything into the chat.
    APPLICANT – a member waiting for acceptance into the chat. Member cannot be demoted to applicants once they have been accepted.

Refer to

    ALTER CHATMEMBER CANSETROLETO command for how to determine if it is possible to change the role of any given chat member.
    ALTER CHATMEMBER SETROLETO command for more info on how to change chat member roles.

ALTER CHAT SETPASSWORD

This command enables you to set password protection to a chat channel.

Syntax:
ALTER CHAT <chat_id> SETPASSWORD <password> <password_hint>

Example:

-> ALTER CHAT #anappo/$a1044019f5dc8c48 SETPASSWORD test2 password is test2
<- ALTER CHAT SETPASSWORD
<- CHAT #anappo/$a1044019f5dc8c48 PASSWORDHINT password is test2

Note that the password must be one word – without any whitespaces in it. All subsequent words in command parameters will be considered as password hint. Password hint will be displayed to users when they join the chat.

Version
Protocol 7 (API version 3.0)

ALTER CHAT ENTERPASSWORD

This command enables you to enter passwords from within your own code, when joining password-protected chat channels.

Syntax:
ALTER CHAT <chat_id> ENTERPASSWORD <password>

Example:

//---------------------------------------------------------------------------
// While trying to connect to a public password-protected channel, 
// we get following messages:
<- CHAT #test_l/$4ea116d4c216baef PASSWORDHINT "password is test"
<- CHAT #test_l/$4ea116d4c216baef MYSTATUS PASSWORD_REQUIRED
<- CHAT #test_l/$4ea116d4c216baef STATUS UNSUBSCRIBED
//---------------------------------------------------------------------------
// Lets supply a wrong password first and see what happens..
-> ALTER CHAT #test_l/$4ea116d4c216baef ENTERPASSWORD test2
<- ALTER CHAT ENTERPASSWORD
<- CHAT #test_l/$4ea116d4c216baef MYSTATUS CONNECTING
<- CHAT #test_l/$4ea116d4c216baef STATUS UNSUBSCRIBED
<- CHAT #test_l/$4ea116d4c216baef MYSTATUS PASSWORD_REQUIRED
<- CHAT #test_l/$4ea116d4c216baef STATUS UNSUBSCRIBED
//---------------------------------------------------------------------------
// Now lets supply correct password:
-> ALTER CHAT #test_l/$4ea116d4c216baef ENTERPASSWORD test
<- ALTER CHAT ENTERPASSWORD
<- CHAT #test_l/$4ea116d4c216baef MYSTATUS CONNECTING
<- CHAT #test_l/$4ea116d4c216baef STATUS UNSUBSCRIBED
<- CHAT #test_l/$4ea116d4c216baef MYSTATUS WAITING_REMOTE_ACCEPT
<- CHAT #test_l/$4ea116d4c216baef STATUS UNSUBSCRIBED
<- CHAT #test_l/$4ea116d4c216baef MYROLE USER
<- CHAT #test_l/$4ea116d4c216baef MEMBERS anappo test_l
<- CHAT #test_l/$4ea116d4c216baef ACTIVEMEMBERS anappo test_l
<- CHAT #test_l/$4ea116d4c216baef TIMESTAMP 1174906897
<- CHAT #test_l/$4ea116d4c216baef ADDER test_l
<- CHAT #test_l/$4ea116d4c216baef GUIDELINES test guidelines
<- MESSAGE 557 STATUS RECEIVED
<- CHAT #test_l/$4ea116d4c216baef TOPIC TestingPublicChats2
<- CHAT #test_l/$4ea116d4c216baef OPTIONS 1
<- CHATMEMBER 556 ROLE LISTENER
<- CHAT #test_l/$4ea116d4c216baef MYROLE LISTENER
<- CHATMEMBER 547 ROLE CREATOR
<- CHAT #test_l/$4ea116d4c216baef MYSTATUS SUBSCRIBED
<- CHAT #test_l/$4ea116d4c216baef STATUS MULTI_SUBSCRIBED
<- CHAT #test_l/$4ea116d4c216baef FRIENDLYNAME TestingPublicChats2
<- MESSAGE 558 STATUS RECEIVED

Version
Protocol 7 (API version 3.0)

ALTER CHAT SETOPTIONS

This command enables you to change chat options.

Syntax:
ALTER CHAT <chat_id> SETOPTIONS <options bitmap>

Chat options bits:

    1 – JOINING_ENABLED – when this bit is off, new users cannot join the chat.
    2 – JOINERS_BECOME_APPLICANTS – when this bit is on, new users will be able to join the chat but they will be unable to post or receive messages until authorized by one of the chat administrators (CREATOR or MASTER).
    4 – JOINERS_BECOME_LISTENERS – when this bit is on, new users will be able to receive message in chat but unable to post until promoted to USER role. Basically a read-only flag for new users.
    8 – HISTORY_DISCLOSED – when this bit is off, newly joined members can see chat history prior to their joining. Maximum amount of history backlog available is either 400 messages or 2 weeks of time, depending on which limit is reached first.
    16 – USERS_ARE_LISTENERS – read-only flag for chat members with USER role.
    32 – TOPIC_AND_PIC_LOCKED_FOR_USERS – when this bit of options is off, USER level chat members can change chat topic and the topic picture.

Example:

//-----------------------------------------------------------------------------
// Setting flags: JOINING_ENABLED, JOINERS_BECOME_LISTENERS, HISTORY_DISCLOSED
// Adding up the bits: 1 + 4 + 8 = 13
-> ALTER CHAT #anappo/$a1044019f5dc8c48 SETOPTIONS 13
<- MESSAGE 678 STATUS SENDING
<- ALTER CHAT SETOPTIONS
<- CHAT #anappo/$a1044019f5dc8c48 OPTIONS 13
<- MESSAGE 678 STATUS SENT

Version
Protocol 7 (API version 3.0)

ALTER CHATMEMBER SETROLETO

This command enables chat administrators (chat CREATORS AND MASTERS) to set privilege levels (roles) for other chat members.

Syntax:
-> ALTER CHATMEMBER <id> SETROLETO CREATOR|MASTER|HELPER|USER|LISTENER

Refer to

    Chat roles section for more information on different roles. Note that you cannot demote a user to LISTENER role when the chat is already in ready-only mode (USERS_ARE_LISTENERS chat option).
    ALTER CHATMEMBER CANSETROLETO command for how to determine if it is possible to change the role of any given chat member.

Example:

-> GET CHAT #anappo/$anappo3;5f7cdbdd32dc731c MEMBEROBJECTS
<- CHAT #anappo/$anappo3;5f7cdbdd32dc731c MEMBEROBJECTS 1846, 2227, 2495
-> GET CHATMEMBER 2495 IDENTITY
<- CHATMEMBER 2495 IDENTITY anappo2
-> GET CHATMEMBER 2495 ROLE
<- CHATMEMBER 2495 ROLE HELPER
-> ALTER CHATMEMBER 2495 SETROLETO USER
<- ALTER CHATMEMBER SETROLETO
<- MESSAGE 2620 STATUS SENDING
<- CHATMEMBER 2495 ROLE USER

Version
Protocol 7 (API version 3.0)

ALTER CHATMEMBER CANSETROLETO

This command can be used to determine weather current user is able to change the privilege level of another chat member.

Syntax:
-> ALTER CHATMEMBER <id> CANSETROLETO CREATOR|MASTER|HELPER|USER|LISTENER|APPLICANT

<- ALTER CHATMEMBER CANSETROLETO TRUE|FALSE

Note that unlike other ALTER commands, this one doesn’t actually change object properties.

Refer to

    Chat roles section for more information on different roles.
    ALTER CHATMEMBER SETROLETO command for more info on how to change chat member roles.

Example:

-> GET CHAT #test/$test3;5f7cdbdd32dc731c MEMBEROBJECTS
<- CHAT #test/$test3;5f7cdbdd32dc731c MEMBEROBJECTS 1846, 2227, 2495
-> GET CHATMEMBER 2495 IDENTITY
<- CHATMEMBER 2495 IDENTITY testuser
-> ALTER CHATMEMBER 2495 CANSETROLETO HELPER
<- ALTER CHATMEMBER CANSETROLETO TRUE
-> ALTER CHATMEMBER 2495 SETROLETO HELPER
<- ALTER CHATMEMBER SETROLETO
<- MESSAGE 3166 STATUS SENDING
<- CHATMEMBER 2495 ROLE HELPER

Version
Protocol 7 (API version 3.0)

ALTER CHAT KICK

With this command, chat member with sufficient privilege level (master or creator) can remove another member from chat.

Note that after being kicked from the channel, the kicked member can re-join the chat. For more permanent removal, see ALTER CHAT KICKBAN command.

Syntax:
ALTER CHAT <chat_id> KICK <skypename1[, skypename2 ..]>

Example:

-> ALTER CHAT  #test/$a1044019f5dc8c48 KICK test2
<- ALTER CHAT KICK

Version
Protocol 7 (API version 3.0)

ALTER CHAT KICKBAN

With this command, chat member with sufficient privilege level (master or creator) can permanently remove another member from chat. Note that kickban only prevents the user from re-joining the chat. Banned users can be added back to the chat by administrators from within the chat.

Syntax:
ALTER CHAT <chat_id> KICKBAN <skypename1[, skypename2 ..]>

Example:

-> ALTER CHAT  #test/$a1044019f5dc8c48 KICKBAN test2
<- ALTER CHAT KICKBAN

Version
Protocol 7 (API version 3.0)

ALTER CHAT FINDUSINGBLOB

This command searches for existing CHAT object with given BLOB property value and returns chat ID and status. Refer to CHAT object for more information.

Syntax:
CHAT FINDUSINGBLOB <blob>

Example:

-> CHAT FINDUSINGBLOB LsgqqqCTpxWYjt9PL1hSvGDOiPhqUuQAHxI7w7Qu7gJ3VZv_q_99ZJO4lF9Dfaw
<- CHAT #anappo2/$d936403094338dbb STATUS MULTI_SUBSCRIBED

Version
Protocol 7 (API version 3.0)

ALTER CHAT CREATEUSINGBLOB

This command creates a chat object, based on public chat blob. This enables you to join public chats from within your own code, assuming that you have somehow obtained the chat blob.

Syntax:
CHAT CREATEUSINGBLOB <blob>

Example:

//------------------------------------------------------------------------------
// What we start is a blob of a public chat we parsed out of a 
// public chat URL or, for example, got sent via another chat.
// that blob is: 6aM81Z5mZRyricRDcjkdy5bf3Y6TsCbVvaxNVVCcYSVsQxRGhlAVmTgpYexh
// First we create a CHAT object.
-> CHAT CREATEUSINGBLOB 6aM81Z5mZRyricRDcjkdy5bf3Y6TsCbVvaxNVVCcYSVsQxRGhlAVmTgpYexh
<- CHAT #anappo/$b9275b3b334341f2 NAME #anappo/$b9275b3b334341f2
<- CHAT #anappo/$b9275b3b334341f2 ACTIVITY_TIMESTAMP 0
<- CHAT #anappo/$b9275b3b334341f2 STATUS UNSUBSCRIBED
<- CHAT #anappo/$b9275b3b334341f2 TYPE MULTICHAT
<- CHAT #anappo/$b9275b3b334341f2 MYSTATUS UNSUBSCRIBED
<- CHAT #anappo/$b9275b3b334341f2 STATUS UNSUBSCRIBED
//------------------------------------------------------------------------------
// Now that we have chat object and it's ID, we can join the chat
-> ALTER CHAT #anappo/$b9275b3b334341f2 JOIN
<- CHAT #anappo/$b9275b3b334341f2 MYSTATUS CONNECTING
<- CHAT #anappo/$b9275b3b334341f2 STATUS UNSUBSCRIBED
<- ALTER CHAT JOIN
//------------------------------------------------------------------------------
// Note that this is our privilege level (role) in this chat
<- CHATMEMBER 293 ROLE USER
<- CHAT #anappo/$b9275b3b334341f2 MEMBERS anappo
<- CHAT #anappo/$b9275b3b334341f2 FRIENDLYNAME Avo Nappo
<- CHAT #anappo/$b9275b3b334341f2 ACTIVEMEMBERS anappo
<- CHAT #anappo/$b9275b3b334341f2 ACTIVITY_TIMESTAMP 1175004600
<- CHAT #anappo/$b9275b3b334341f2 BOOKMARKED TRUE
<- CHAT #anappo/$b9275b3b334341f2 MEMBERS anappo anappo4
<- CHAT #anappo/$b9275b3b334341f2 FRIENDLYNAME Avo Nappo, anappo4
<- CHAT #anappo/$b9275b3b334341f2 ACTIVEMEMBERS anappo anappo4
<- CHAT #anappo/$b9275b3b334341f2 MYSTATUS WAITING_REMOTE_ACCEPT
<- CHAT #anappo/$b9275b3b334341f2 STATUS UNSUBSCRIBED
<- CHATMEMBER 294 IS_ACTIVE FALSE
<- CHAT #anappo/$b9275b3b334341f2 MYROLE USER
<- CHAT #anappo/$b9275b3b334341f2 MEMBERS anappo anappo4 test_p
<- MESSAGE 298 STATUS RECEIVED
<- CHAT #anappo/$b9275b3b334341f2 ACTIVEMEMBERS anappo test_p
<- CHAT #anappo/$b9275b3b334341f2 TIMESTAMP 1175003077
<- CHAT #anappo/$b9275b3b334341f2 ADDER anappo
<- CHAT #anappo/$b9275b3b334341f2 TOPIC TestingPublicChat3
<- CHAT #anappo/$b9275b3b334341f2 OPTIONS 1
//------------------------------------------------------------------------------
// Following notification tells us chatmember ID of the chat owner (creator)
<- CHATMEMBER 293 ROLE CREATOR
<- CHAT #anappo/$b9275b3b334341f2 MYSTATUS SUBSCRIBED
<- CHAT #anappo/$b9275b3b334341f2 STATUS MULTI_SUBSCRIBED
<- CHAT #anappo/$b9275b3b334341f2 FRIENDLYNAME TestingPublicChat3
<- MESSAGE 299 STATUS RECEIVED
<- CHATMEMBER 294 IS_ACTIVE TRUE
<- CHAT #anappo/$b9275b3b334341f2 ACTIVEMEMBERS anappo anappo4 test_p
//------------------------------------------------------------------------------
// We can use GET CHATMEMBER 293 IDENTITY to get creator's Skypename
-> GET CHATMEMBER 293 IDENTITY
<- CHATMEMBER 293 IDENTITY anappo
//------------------------------------------------------------------------------
// Opening chat window in UI
-> OPEN CHAT #anappo/$b9275b3b334341f2
<- OPEN CHAT #anappo/$b9275b3b334341f2

Version
Protocol 7 (API version 3.0)

ALTER CHAT SETGUIDELINES

This command enables you to set the Guidelines message for public chats. The guideline message is displayed at the top of the chat window.

Syntax:
ALTER CHAT <chat_id> SETGUIDELINES <guidelines>

Example:

-> ALTER CHAT #anappo/$a1044019f5dc8c48 SETGUIDELINES these here are test guidelines
<- MESSAGE 744 STATUS SENDING
<- ALTER CHAT SETGUIDELINES
<- CHAT #anappo/$a1044019f5dc8c48 GUIDELINES these here are test guidelines
<- MESSAGE 744 STATUS SENT

Version
Protocol 7 (API version 3.0)*/