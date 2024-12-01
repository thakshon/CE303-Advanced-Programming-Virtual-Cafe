
# CE303 - Advanced Programming: Virtual Cafe (Java)

A **"Virtual Cafe"** implemented in Java using a client-server architecture. This project demonstrates advanced programming skills by simulating a dynamic and interactive virtual cafe system where users can place orders, communicate with the server, and enjoy a user-friendly interface.

---

## 📖 Overview

This project was developed as part of the **CE303 Advanced Programming** module. It combines theoretical knowledge and practical skills to create a feature-rich virtual cafe system. The project highlights include:
- **Client-server architecture** for seamless communication.
- A clean, intuitive user interface for both client and server sides.
- Robust error handling to ensure reliability even in edge cases.

---

## 📋 Project Details

### 🔗 Full Specification
Detailed project specifications are provided in [VirtualCafe.html](VirtualCafe.html).

### 📚 Documentation
Comprehensive technical and user documentation is available here:  
[VirtualCafe Documentation](dir:VirtualCafe%20Documentation).

### 🛠️ Implementation
A detailed explanation of the implementation, covering the system's architecture, design patterns, and features, is documented in:  
[CE303 VirtualCafe Implementation.pdf](CE303%20VirtualCafe%20Implementation.pdf).

---

## 🌟 Key Features

### 🖧 Client-Server Communication  
A reliable and efficient system to manage requests and responses between the client and the server.

### 👩‍💻 User-Friendly Interface
- Intuitive and aesthetically pleasing design for both ends of the system.
- Clear instructions and easy navigation.

### 🛡️ Robust Error Handling
- Handles invalid inputs gracefully, e.g., "order -1 teas".
- Provides users with error messages and suggestions for corrections.

### 🍽️ Customizable Orders
- Supports placing detailed orders with specific quantities and multiple menu items.

### 🧩 Modular Code
- Clean and maintainable Java code.
- Incorporates advanced programming techniques and well-known design patterns.

---

## 📊 Performance and Evaluation

### 💬 Feedback
> *"Everything worked perfectly. You had great quality code, an excellent user interface on both the client and server sides, and only the odd bug that I worked VERY hard to uncover (e.g., 'order -1 teas')."*  

---

## 🚀 Usage

### 📌 Prerequisites
- Java 8 or later.
- An IDE (e.g., IntelliJ IDEA, Eclipse) or a terminal for execution.

### 🏃 Running the Project
1. Clone the repository:
   ```bash
   git clone <repository_url>
   cd CE303-Advanced-Programming-Virtual-Cafe-Java
   ```

2. Compile the source code:
   ```bash
   javac -d bin src/*.java
   ```

3. Start the server:
   ```bash
   java -cp bin Server
   ```

4. Start the client:
   ```bash
   java -cp bin Client
   ```

5. Follow the on-screen instructions to interact with the Virtual Cafe.

---

## 📂 File Structure

```plaintext
CE303-Advanced-Programming-Virtual-Cafe-Java/
## Project Directory Structure

```plaintext
├───CE303-Advanced-Programming-Virtual-Cafe-Java-main.iml   # IntelliJ project configuration file
├───LICENSE                                                # Project license file
├───Log.json                                                # Logs in JSON format
├───README.md                                              # This README file
├───VirtualCafe.html                                        # Project specification document
│
├───.idea                                                   # IDE configuration files
│   ├───libraries                                           # Libraries used in the project
│   │   └───gson.xml                                        # Gson library configuration
│   ├───misc.xml                                            # Miscellaneous IDE settings
│   ├───modules.xml                                         # Modules configuration
│   └───workspace.xml                                       # Workspace configuration
│
├───out                                                     # Compiled output directory
│   └───production                                          # Production build
│       └───CE303-Advanced-Programming-Virtual-Cafe-Java-main
│           ├───Helpers                                     # Helper classes
│           │   ├───CustomerHelpers                         # Customer-related helper classes
│           │   ├───Drinks                                  # Drink-related helper classes
│           │   └───ServerHelpers                           # Server-side helper classes
│           ├───Barista.class                               # Barista class (compiled)
│           ├───Customer.class                              # Customer class (compiled)
│           ├───gson.jar                                     # Gson library
│           └───Log.json                                     # Logs (compiled)
│
├───src                                                     # Source code files
│   ├───Barista.java                                        # Barista class (source)
│   ├───Customer.java                                       # Customer class (source)
│   ├───gson.jar                                            # Gson library
│   ├───Log.json                                            # Logs (source)
│   └───Helpers                                             # Helper classes
│       ├───CustomerHelpers                                 # Customer-related helper classes
│       ├───Drinks                                          # Drink-related helper classes
│       └───ServerHelpers                                   # Server-side helper classes
│
└───VirtualCafe Documentation                               # Documentation for the project
    ├───Helpers                                             # Documentation for helper classes
    ├───index-files                                         # Index files for JavaDocs
    ├───legal                                                # License and legal information
    ├───resources                                            # Resources such as images
    └───script-dir                                           # JavaScript and CSS resources
        ├───jquery-3.6.0.min.js                             # jQuery library
        ├───jquery-ui.min.css                               # jQuery UI stylesheet
        └───jquery-ui.min.js                                # jQuery UI library
```

---

## 🤝 Contribution

Contributions are welcome!  
Feel free to fork the repository, make improvements, and submit a pull request. 

---

## ⚖️ License

This project is licensed under the [MIT License](LICENSE).  

---

**Disclaimer:**  
Academic integrity is paramount. Any form of unauthorized replication of this project could result in severe academic consequences. Always adhere to your institution's policies.
