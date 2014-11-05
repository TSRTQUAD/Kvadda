function rawtrajectory = getTrajectory( tmpcostmat, nodes, startpoint )
% The TSP-solver kan only handle integers, rescale the costmatrix to fit
% each element in an 32-bit unsigned variable.
normalizationfactor = 2^16/max([max(max(tmpcostmat.lon)),max(max...
    (tmpcostmat.lat)),max(max(tmpcostmat.cross))]);

pattern = {'lon' 'lat' 'cross'};
bestsolution = inf;
for ii = 1:3
    costmat = round(normalizationfactor*tmpcostmat.(pattern{ii}));
    trajectory.(pattern{ii}) = tspsolver(nodes,costmat);
    
    % Calculate the total trajectory distance and sort out the shortest
    distance = 0;
    for kk = 2:length(trajectory.(pattern{ii}))
        distance = distance + lldistkm(trajectory...
            .(pattern{ii})(kk-1,:),trajectory.(pattern{ii})(kk,:));
    end
    % THIS IS WHERE A FUNCTION THAT EVALUATES EACH TRAJECTORY BASED ON BOTH
    % NUMBER OF TURNS AND DISTANCE SHOULD BE IMPLEMENTED.
    currentsolution = distance;
    if currentsolution < bestsolution
        besttrajectory = trajectory.(pattern{ii});
        bestsolution = currentsolution;
    end
end
% Add the startpoint to the beginning and end of the trajectory icluding
% some points inbetween to straighten up the future interpolation
startpointslat = min(startpoint(1),besttrajectory(1,1)):...
    abs(besttrajectory(1,1)-startpoint(1))/4:max(startpoint(1),...
    besttrajectory(1,1));
if startpoint(1) > besttrajectory(1,1)
    startpointslat = fliplr(startpointslat);
end
startpointslon = interp1([startpoint(1) besttrajectory(1,1)],...
    [startpoint(2) besttrajectory(1,2)],startpointslat);
endpointslat = min(startpoint(1),besttrajectory(end,1)):...
    abs(besttrajectory(end,1)-startpoint(1))/4:max(startpoint(1),...
    besttrajectory(end,1));
if startpoint(1) < besttrajectory(end,1)
    endpointslat = fliplr(endpointslat);
end
endpointslon = interp1([startpoint(1) besttrajectory(end,1)],...
    [startpoint(2) besttrajectory(end,2)],endpointslat);

rawtrajectory = [[startpointslat(1:4)' startpointslon(1:4)'];...
    besttrajectory;[endpointslat(2:5)' endpointslon(2:5)']];

end