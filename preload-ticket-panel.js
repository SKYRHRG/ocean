const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('panel', {
  onInit: (callback) => {
    const handler = (_e, data) => callback(data);
    ipcRenderer.on('panel:init', handler);
    return () => ipcRenderer.removeListener('panel:init', handler);
  },
  launchRun: (opts) => ipcRenderer.invoke('panels:launch-run', opts),
  openPair2: (journeyId) => ipcRenderer.invoke('panels:open-pair-2', { journeyId }),
  openNextPair: (journeyId) => ipcRenderer.invoke('panels:open-next-pair', { journeyId }),
  setPairCredential: (journeyId, pairIndex, credentialId) =>
    ipcRenderer.invoke('panels:set-pair-credential', {
      journeyId,
      pairIndex,
      credentialId,
    }),
  setIpChoice: (opts) => ipcRenderer.invoke('panels:set-ip-choice', opts || {}),
  close: () => ipcRenderer.invoke('panels:close-self'),
  listRuns: () => ipcRenderer.invoke('runs:list'),
  proceedPtFare: (sessionId, proceed = true) =>
    ipcRenderer.invoke('panels:pt-fare-proceed', { sessionId, proceed }),
  onPanelStatus: (callback) => {
    const handler = (_e, data) => callback(data);
    ipcRenderer.on('panel:status', handler);
    return () => ipcRenderer.removeListener('panel:status', handler);
  },
  onPanelPtFare: (callback) => {
    const handler = (_e, data) => callback(data);
    ipcRenderer.on('panel:pt-fare', handler);
    return () => ipcRenderer.removeListener('panel:pt-fare', handler);
  },
  onPanelQr: (callback) => {
    const handler = (_e, data) => callback(data);
    ipcRenderer.on('panel:qr', handler);
    return () => ipcRenderer.removeListener('panel:qr', handler);
  },
  onPanelQrHide: (callback) => {
    const handler = (_e, data) => callback(data);
    ipcRenderer.on('panel:qr-hide', handler);
    return () => ipcRenderer.removeListener('panel:qr-hide', handler);
  },
  onPrivacyMask: (callback) => {
    const handler = () => callback();
    ipcRenderer.on('panel:privacy-mask', handler);
    return () => ipcRenderer.removeListener('panel:privacy-mask', handler);
  },
  onPrivacyRestore: (callback) => {
    const handler = () => callback();
    ipcRenderer.on('panel:privacy-restore', handler);
    return () => ipcRenderer.removeListener('panel:privacy-restore', handler);
  },
});
