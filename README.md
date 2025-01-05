# Hotel Management System

A comprehensive hotel management system built with Java and JavaFX that allows hotel owners, managers, and receptionists to manage their hotel operations efficiently.

## Team Members
- Berkin TEZEL (22221010)
- Bartu COKGULEN (22221008)

## Features

### For Hotel Owners
- Create and manage manager accounts
- Add and delete rooms
- View client information and ratings
- Monitor additional services usage

### For Managers
- Create and manage receptionist accounts
- View notifications
- Monitor hotel operations

### For Receptionists
- Manage room reservations
- Handle client check-in/check-out
- Process additional services
- Manage client ratings

## Technical Details

### Built With
- Java
- JavaFX for the user interface
- MySQL database
- Scene Builder for UI design

### Key Components
- Multi-level user authentication (Owner, Manager, Receptionist)
- Real-time room availability tracking
- Client management system
- Rating and feedback system
- Additional services management

## Database Structure
- Users (with role-based access)
- Rooms
- Clients
- Reservations
- Client Ratings
- Additional Services

## Installation

1. Clone the repository
```bash
git clone https://github.com/yourusername/Hotel-Management.git
```

2. Set up MySQL database
- Create a new database
- Import the provided SQL schema
- Configure database connection in `DatabaseConnection.java`

3. Run the application
```bash
cd Hotel-Management
# Run with your preferred Java IDE or build tool
```

## Usage

1. Login with appropriate credentials based on role:
   - Owner
   - Manager
   - Receptionist

2. Navigate through different panels based on your role:
   - Owner Panel: Manage rooms, view statistics
   - Manager Panel: Handle staff, view reports
   - Receptionist Panel: Handle bookings, client services

## Contributing
This project was developed as part of our coursework. Feel free to fork and enhance it further.

## License
This project is licensed under the MIT License - see the LICENSE file for details.
