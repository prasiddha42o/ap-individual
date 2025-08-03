# Student Course Registration System

A simple JavaFX desktop application for managing student course registrations with comprehensive data storage in text files.

## Features

- User Authentication with file-based storage
- Compact Dashboard with essential information
- Student Profile Management
- Course Registration and Management
- Analytics and Reports with charts
- Complete data persistence in .txt files
- Activity logging and tracking

## Data Files

The application creates and manages the following .txt files in the `data/` directory:

- `students.txt` - Student information and registered courses
- `courses.txt` - Available course catalog
- `registrations.txt` - Registration activity log
- `system_logs.txt` - System activity tracking
- `analytics_data.txt` - Analytics and statistics data

## System Requirements

- Java 11 or higher
- JavaFX 17.0.2
- Maven 3.6+

## Installation

1. Extract the project files
2. Navigate to the project directory
3. Build: `mvn clean compile`
4. Run: `mvn javafx:run`

## Default Login Credentials

- Student 1: Username: `STU001`, Password: `password123`
- Student 2: Username: `STU002`, Password: `password456`
- Admin: Username: `admin`, Password: `admin`

## File Structure

\`\`\`
data/
├── students.txt          # Student data with registered courses
├── courses.txt           # Course catalog
├── registrations.txt     # Registration activity log
├── system_logs.txt       # System activity tracking
└── analytics_data.txt    # Analytics data storage
\`\`\`

## Features

### Dashboard
- Clean, simple interface
- Real-time clock and statistics
- Quick navigation to all features

### Analytics
- Student distribution by courses (Pie and Bar charts)
- Program distribution analysis
- Credits distribution tracking
- Detailed analytics reports

### Data Management
- Automatic file creation and initialization
- Data backup before updates
- Comprehensive logging system
- Error handling and validation

## Technical Details

- MVC architecture pattern
- File-based data persistence
- Real-time data updates
- Responsive UI design
- Comprehensive error handling
