package cn.com.qjun.ddns.service;

import cn.com.qjun.ddns.vo.DNSInfo;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.AddDomainRecordRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.exceptions.ClientException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Ryan
 * @date 2018/5/5
 */
@Service
public class DnsService {
    private static final String TYPE_A = "A";

    @Autowired
    private IAcsClient iAcsClient;

    public void addRecord(DNSInfo dnsInfo) throws ClientException {
        AddDomainRecordRequest request = new AddDomainRecordRequest();
        request.setDomainName(dnsInfo.getDomainName());
        request.setRR(dnsInfo.getRr());
        request.setType(dnsInfo.getRecordType().getValue());
        request.setValue(dnsInfo.getValue());
        iAcsClient.getAcsResponse(request);
    }

    public void updateRecord(String recordId, DNSInfo dnsInfo) throws ClientException {
        UpdateDomainRecordRequest request = new UpdateDomainRecordRequest();
        request.setRecordId(recordId);
        request.setRR(dnsInfo.getRr());
        request.setType(dnsInfo.getRecordType().getValue());
        request.setValue(dnsInfo.getValue());
        iAcsClient.getAcsResponse(request);
    }

    public List<DescribeDomainRecordsResponse.Record> getRecordList(String domainName) throws ClientException {
        DescribeDomainRecordsRequest request = new DescribeDomainRecordsRequest();
        request.setDomainName(domainName);
        DescribeDomainRecordsResponse recordsResponse = iAcsClient.getAcsResponse(request);
        return recordsResponse.getDomainRecords();
    }
}
