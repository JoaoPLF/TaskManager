# Task Manager (WIP)

Inspired by [Skylight Calendar](https://www.skylightframe.com/calendar/) and its unavailability outside of NA and EU, I decided to build my own version of it.

### The plan so far

The idea for this first prototype is to build a server-client system using sockets. Both client and server work in the same way, having the same functionalities when it comes to adding events, tasks, notes. Clients are independent and should be 100% usable offline, while the server will act as the source of truth - clients can sync to it so updates can be shared between devices.

After this first part is done, I'll explore building a custom android rom that automatically runs the app on boot. And finally, I'll hopefully have the resources to develop the iOS app.

- [X] Server/client socket communication
- [ ] Store data in SQLite
- [ ] Send data between devices
- [ ] Conflict resolution
- [ ] Add calendar and To-Dos
- [ ] Buy tablet/touchscreen computer and develop custom android rom
- [ ] Buy a macbook, an iPhone and develop the iOS native modules
