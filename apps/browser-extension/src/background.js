const KEY = 'clearchoice-state';
const defaults = { enabled:true, paused:false, hosts:{} };
async function state() { return { ...defaults, ...(await chrome.storage.local.get(KEY))[KEY] }; }
chrome.runtime.onInstalled.addListener(async () => { if (!(await chrome.storage.local.get(KEY))[KEY]) await chrome.storage.local.set({ [KEY]:defaults }); });
chrome.runtime.onMessage.addListener((message, sender, respond) => {
  (async () => {
    const s = await state();
    if (message?.type === 'GET_STATE') return s;
    if (message?.type === 'SET_STATE') {
      if (sender.url !== chrome.runtime.getURL('popup.html')) throw new Error('Only the extension popup can change settings.');
      const next = { ...s, ...message.patch };
      await chrome.storage.local.set({ [KEY]:next });
      const tabs = await chrome.tabs.query({});
      for (const tab of tabs) if (tab.id != null) chrome.tabs.sendMessage(tab.id,{ type:'STATE_CHANGED', state:next }).catch(() => {});
      return next;
    }
    if (message?.type === 'SET_SITE') {
      if (sender.url !== chrome.runtime.getURL('popup.html')) throw new Error('Only the extension popup can change site permissions.');
      const origin = message.origin;
      const tabId = message.tabId;
      if (!origin || !/^https?:\/\//.test(origin)) throw new Error('This page cannot be enabled.');
      const scriptId = `cc2-${origin.replace(/[^a-z0-9]/gi,c => `_${c.charCodeAt(0).toString(16)}_`)}`; // injective: distinct origins never share an id
      const legacyId = `cc-${btoa(origin).replace(/[^a-z0-9]/gi,'').slice(0,24)}`;
      if (message.enabled) {
        if (!await chrome.permissions.contains({ origins:[`${origin}/*`] })) throw new Error('Site permission is not granted.');
        await chrome.scripting.registerContentScripts([{ id:scriptId, matches:[`${origin}/*`], js:['src/content.js'], runAt:'document_idle', allFrames:false, persistAcrossSessions:true }]).catch(async e => { if (!String(e).includes('Duplicate')) throw e; });
        if (tabId != null) await chrome.scripting.executeScript({ target:{tabId}, files:['src/content.js'] });
      } else {
        const originTabs = await chrome.tabs.query({ url:`${origin}/*` }).catch(() => []);
        const targets = new Set(originTabs.map(t => t.id).filter(id => id != null));
        if (tabId != null) targets.add(tabId);
        for (const id of targets) await chrome.tabs.sendMessage(id,{ type:'RESTORE' }).catch(() => {});
        await chrome.scripting.unregisterContentScripts({ ids:[scriptId,legacyId] }).catch(() => {});
      }
      const hosts = { ...s.hosts, [origin]:message.enabled === true };
      const next={ ...s, hosts };
      await chrome.storage.local.set({ [KEY]:next });
      if (message.enabled && tabId != null) await chrome.tabs.sendMessage(tabId,{type:'STATE_CHANGED',state:next}).catch(() => {});
      return next;
    }
    throw new Error('Unknown message');
  })().then(respond).catch(error => respond({ error:error.message }));
  return true;
});
