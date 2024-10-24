import { NativeModulesProxy, EventEmitter, Subscription } from 'expo-modules-core';

// Import the native module. On web, it will be resolved to Socket.web.ts
// and on native platforms to Socket.ts
import ServerSocketModule from './src/ServerSocketModule';
import SocketModule from './src/SocketModule';
import { ChangeEventPayload } from './src/Socket.types';

// Get the native constant value.
export const PI = SocketModule.PI;

export function hello(): string {
  return SocketModule.hello();
}

export async function setValueAsync(value: string) {
  return await SocketModule.setValueAsync(value);
}

const emitter = new EventEmitter(SocketModule ?? NativeModulesProxy.Socket);

export function addChangeListener(listener: (event: ChangeEventPayload) => void): Subscription {
  return emitter.addListener<ChangeEventPayload>('onChange', listener);
}

export async function startServer() {
  return await ServerSocketModule.startServer();
}

export async function startClient() {
  return await SocketModule.startClient();
}

export { ChangeEventPayload };
