


export function toSIP(frame) {
    const headers = frame.header.map(h => `${h.name}: ${h.value}`).join('\r\n');
    return `SIP/2.0 ${frame.statusCode} ${frame.reasonPhrase}\r\n${headers}\r\n${frame.body ?? ""}`;
}