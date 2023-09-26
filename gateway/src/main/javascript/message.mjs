


export function toSIP(frame) {
    const headers = frame.headers?.flatMap(h => h.values?.map(value => `${h.name}: ${value}`))?.join('\r\n');
    return `SIP/2.0 ${frame.statusCode} ${frame.reasonPhrase}\r\n${headers}\r\n${frame.body ?? ""}`;
}