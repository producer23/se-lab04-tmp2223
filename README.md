https://hyperskill.org/projects/52/stages/286/implement

Description

In this stage, you will write a program that sends real text and image files to the server. The server keeps the files and sends them back on request until you decide to delete them.

Also, the server should be able to recognize each file by its unique identifier. If the file is created successfully, the server should output an integer identifier after the code 200 and a single space. If the creation of the file failed, no identifier is needed.

From now on, you'll be able to access the file on the server using either its identifier or the filename. To do that, after the GET or DELETE command, you should specify whether you want to use the file id or the name. Let's use BY_ID and BY_NAME as keywords. Every time you want to get a file from the server, you can write either GET BY_ID 12 or GET BY_NAME filename.txt. The same applies to DELETE BY_ID and DELETE BY_NAME.Note that you don't need these keywords with the PUT method: it just saves a new file on the server, and the server sends you a new file id.

Of course, most files are usually much larger than just one line of text. The process of saving larger files may take a while, so you should use a parallel approach. For example, you may want to use executors, and every time the client sends a request, you should perform the requested task in the thread pool, not in the main thread. The main thread should just wait for another request. Note that the map of identifiers to the file names should be used synchronously because different threads have access to it. Do not forget to save this map somewhere because you don't want to lose it when the server reboots. After rebooting the server, check that the ID generation process does not start right away.

Notice that you can't send bytes via text stream: while bytes require a single-byte stream, a text stream is variable-byte (this is because Strings in Java are encoded in the UTF format when they are sent using DataInputStream). The solution is sending the byte array itself, not the bytes encoded as text. When you receive an array of bytes, you can't really say where the end of the stream is, unlike the case with the readUTF method where you read only one string at a time. This problem can be solved by adding the number of bytes at the very start of the stream. The sender calculates and sends this number followed by the byte stream, and the receiver reads the number and then reads the stream with the specified number of bytes. You can use the following snippet from stack overflow to understand how this works:

byte[] message = ...
Socket socket = ...
DataOutputStream output = ...

/* after writing some other data */

output.writeInt(message.length); // write length of the message
output.write(message);           // write the message

Socket socket = ...
DataInputStream input = ...

/* after reading some other data */

int length = input.readInt();                // read length of incoming message
byte[] message = new byte[length];
input.readFully(message, 0, message.length); // read the message

In this stage, you should write a client program that prompts the user for action. If they want to save a file on the server, the program should ask the user which file from the ../client/data folder needs to be saved. After that, the user should specify the name of the file (the name should not contain spaces or tabs). If the user doesn't want to specify the name, they should just press Enter without typing anything. The server should generate a unique name for this file and send back the id. The file should be saved in the .../server/data/ folder. Create your own implementation of this behavior.

If the user wants to get a file, the client program should ask if the user wants to use the id or the name of the file. After entering the id or the name, the user must specify the name under which the file should be saved. The file should be saved in the .../client/data/ folder.

If the user wants to delete a file, the client program should ask if the user wants to use the id or the name of the file. After either the id or the name has been entered, the program should send the request to the server.

Since the server cannot shut down by itself and the tests require that the program stops at a certain point, you should implement a simple way to stop the server. The client should be able to handle the exit action and send the respective message to the server. When the client sends exit, you should stop the server. Note: you shouldn't allow this behavior in a normal situation when no testing needs to be done.
Objectives

In this stage, your client-side program should:

    Prompt the user to enter an action.
    For the GET and DELETE action, ask the user if they want to GET or DELETE the file BY_ID or BY_NAME (not required for PUT).
    Prompt the user to enter the content of the file (when applicable).
    Send the request to the server and receive a response from the server.
    Print the respective message after receiving the response and ask the user where they would like to save the received file (when applicable).
    Disconnect from the server and terminate.

The server-side program should:

    Print the Server started! message when the program starts.
    Receive a request from the client and respond accordingly.
    Send a response depending on the type of request:

    For a PUT request, send a status code 200 and a unique INTEGER IDENTIFIER separated by a single space if the file is created successfully; otherwise, send a status code 403.
    For a GET request, send a 200 status code and the FILE_CONTENT separated by a single space if the file exists; otherwise, send a 404 status code.
    For a DELETE request, send a 200 status code if the file is deleted successfully; otherwise, send a 404 status code.

4. The server program should not terminate until it receives the exit command.
Examples

The greater-than symbol followed by a space (> ) represents the user input. Note that it's not part of the input.

Example 1

Enter action (1 - get a file, 2 - save a file, 3 - delete a file): > 2
Enter name of the file: > my_cat.jpg
Enter name of the file to be saved on server: > 
The request was sent.
Response says that file is saved! ID = 23

Example 2

Enter action (1 - get a file, 2 - save a file, 3 - delete a file): > 1
Do you want to get the file by name or by id (1 - name, 2 - id): > 2
Enter id: > 23
The request was sent.
The file was downloaded! Specify a name for it: > cat.jpg
File saved on the hard drive!

Example 3

Enter action (1 - get a file, 2 - save a file, 3 - delete a file): > 3
Do you want to delete the file by name or by id (1 - name, 2 - id): > 2
Enter id: > 23
The request was sent.
The response says that this file was deleted successfully!

Example 4

Enter action (1 - get a file, 2 - save a file, 3 - delete a file): > 3
Do you want to delete the file by name or by id (1 - name, 2 - id): > 2
Enter id: > 23
The request was sent.
The response says that this file is not found!

Example 5

Enter action (1 - get a file, 2 - save a file, 3 - delete a file): > exit
The request was sent.
